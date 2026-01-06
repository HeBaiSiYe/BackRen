package cn.edu.aynu.siyemanager.controller;

import cn.edu.aynu.siyemanager.dto.CertificateAddDTO;
import cn.edu.aynu.siyemanager.dto.CertificateQueryDTO;
import cn.edu.aynu.siyemanager.dto.CertificateReviewDTO;
import cn.edu.aynu.siyemanager.service.CertificateService;
import cn.edu.aynu.siyemanager.vo.CertificateAddVO;
import cn.edu.aynu.siyemanager.vo.PageResult;
import cn.edu.aynu.siyemanager.vo.CertificateListVO;
import cn.edu.aynu.siyemanager.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    /**
     * 6.1 获取证件列表
     */
    @GetMapping("/list")
    public Result<PageResult<CertificateListVO>> getCertificateList(CertificateQueryDTO queryDTO) {
        PageResult<CertificateListVO> result = certificateService.getCertificateList(queryDTO);
        return Result.success(result);
    }

    /**
     * 办理证件（办理中）
     */
    @PostMapping("/add")
    public Result<CertificateAddVO> addCertificate(@RequestBody CertificateAddDTO addDTO) {
        try {
            CertificateAddVO result = certificateService.addCertificate(addDTO);
            return Result.success("证件办理申请已提交", result);
        } catch (Exception e) {
            return Result.error("证件办理失败: " + e.getMessage());
        }
    }

    /**
     * 注销证件
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelCertificate(@PathVariable Long id) {
        try {
            boolean success = certificateService.cancelCertificate(id);
            if (success) {
                return Result.success("证件注销成功");
            } else {
                return Result.error(404, "证件不存在");
            }
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Result.error(409, e.getMessage());
        } catch (Exception e) {
            return Result.error("注销失败: " + e.getMessage());
        }
    }

    /**
     * 证件申请审核
     */
    @PutMapping("/{id}/review")
    public Result<String> reviewCertificate(@PathVariable Long id,
                                            @RequestBody CertificateReviewDTO reviewDTO) {
        if (id == null || id <= 0) {
            return Result.error("证件ID不能为空");
        }
        if (reviewDTO.getApproved() == null) {
            return Result.error("审核结果不能为空");
        }

        try {
            boolean success = certificateService.reviewCertificate(id, reviewDTO.getApproved());
            if (success) {
                return Result.success("证件审核完成");
            } else {
                return Result.error(404, "证件不存在");
            }
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error("审核失败: " + e.getMessage());
        }
    }

    /**
     * 修改证件状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateCertificateStatus(@PathVariable Long id,
                                                  @RequestBody UpdateStatusRequest request) {
        if (id == null || id <= 0) {
            return Result.error("证件ID不能为空");
        }
        if (request.getStatus() == null) {
            return Result.error("状态码不能为空");
        }

        try {
            boolean success = certificateService.updateCertificateStatus(id, request.getStatus());
            if (success) {
                return Result.success("证件状态更新成功");
            } else {
                return Result.error(404, "证件不存在");
            }
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error("状态更新失败");
        }
    }

    /**
     * 内部类：状态更新请求
     */
    private static class UpdateStatusRequest {
        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}