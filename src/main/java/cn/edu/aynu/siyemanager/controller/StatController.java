package cn.edu.aynu.siyemanager.controller;

import cn.edu.aynu.siyemanager.service.StatService;
import cn.edu.aynu.siyemanager.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stat")
public class StatController {

    @Autowired
    private StatService statService;

    /**
     * 获取人员总数
     */
    @GetMapping("/person/count")
    public Result<Long> getPersonCount() {
        long count = statService.getPersonCount();
        return Result.success(count);
    }

    /**
     * 获取户籍总数
     */
    @GetMapping("/household/count")
    public Result<Long> getHouseholdCount() {
        long count = statService.getHouseholdCount();
        return Result.success(count);
    }

    /**
     * 获取证件总数
     */
    @GetMapping("/certificate/count")
    public Result<Long> getCertificateCount() {
        long count = statService.getCertificateCount();
        return Result.success(count);
    }

    /**
     * 获取办理中证件总数
     */
    @GetMapping("/certificate/processing-count")
    public Result<Long> getProcessingCertificateCount() {
        long count = statService.getProcessingCertificateCount();
        return Result.success(count);
    }
}