package cn.edu.aynu.siyemanager.service;

import cn.edu.aynu.siyemanager.dto.CertificateAddDTO;
import cn.edu.aynu.siyemanager.dto.CertificateQueryDTO;
import cn.edu.aynu.siyemanager.vo.CertificateAddVO;
import cn.edu.aynu.siyemanager.vo.PageResult;
import cn.edu.aynu.siyemanager.vo.CertificateListVO;

public interface CertificateService {

    /**
     * 6.1 获取证件列表
     */
    PageResult<CertificateListVO> getCertificateList(CertificateQueryDTO queryDTO);

    /**
     * 办理证件（办理中）
     */
    CertificateAddVO addCertificate(CertificateAddDTO addDTO);

    /**
     * 注销证件
     */
    boolean cancelCertificate(Long id);

    boolean reviewCertificate(Long id, Boolean approved);

    boolean updateCertificateStatus(Long id, Integer status);
}