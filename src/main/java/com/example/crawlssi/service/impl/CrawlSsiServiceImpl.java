package com.example.crawlssi.service.impl;

import com.example.crawlssi.outgate.ssi.SsiClient;
import com.example.crawlssi.outgate.ssi.request.CompanyProfileReqModel;
import com.example.crawlssi.outgate.ssi.response.*;
import com.example.crawlssi.repository.*;
import com.example.crawlssi.repository.entity.*;
import com.example.crawlssi.service.CrawlSsiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CrawlSsiServiceImpl implements CrawlSsiService {

    private final SsiClient ssiClient;
    private final CompanyProfileRepository companyProfileRepository;


    @Autowired
    public CrawlSsiServiceImpl(
            SsiClient ssiClient,
            CompanyProfileRepository companyProfileRepository
    ) {
        this.companyProfileRepository = companyProfileRepository;
        this.ssiClient = ssiClient;
    }

    private void saveCompanyProfileToDb(CompanyProfileResp companyProfileResp) {
        CompanyProfile companyProfile = new CompanyProfile();
        BeanUtils.copyProperties(companyProfileResp.getData().getCompanyProfile(), companyProfile);
        try {
            companyProfileRepository.save(companyProfile);
        } catch (Exception e) {
            log.info("Failed to save company profile to DB: {} to DB --> {} - {}"
                    , companyProfile, e.getMessage(), e.getCause());
        }
    }

    private void crawlAndSaveCompanyProfile(String symbol) {
        CompanyProfileReqModel companyProfileReqModel = new CompanyProfileReqModel();

        companyProfileReqModel.setSymbol(symbol);
        try {
            CompanyProfileResp companyProfileResp =
                    ssiClient.getCompanyProfile(companyProfileReqModel);
            CompletableFuture.runAsync(() -> saveCompanyProfileToDb(companyProfileResp));
        } catch (Exception e) {
            log.info("Error when crawl company profile --> {} - {}", e.getMessage(), e.getCause());
        }
    }

    @Override
    public CompletableFuture<Void> crawlCompanyProfile() {
        return CompletableFuture.runAsync(() -> {
            List<SingleStockModel> listSingleStock = ssiClient.getAllStock().getData();
            listSingleStock.parallelStream().forEach(item -> crawlAndSaveCompanyProfile(item.getCode()));
        });
    }

}
