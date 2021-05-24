package com.example.crawlssi.schedulers;

import com.example.crawlssi.service.CrawlSsiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class CompanyProfileCrawlSchedule {

    private final CrawlSsiService crawlSsiService;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");


    public CompanyProfileCrawlSchedule(
            CrawlSsiService crawlSsiService
    ) {
        this.crawlSsiService = crawlSsiService;
    }

    @Scheduled(cron = "${crawl.cron}")
    public void crawlEach12Hour() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Start crawl companyProfile at {} ", dtf.format(now));
        crawlSsiService.crawlCompanyProfile().join();
        log.info("Done crawl companyProfile at {} ", dtf.format(now));
    }

}
