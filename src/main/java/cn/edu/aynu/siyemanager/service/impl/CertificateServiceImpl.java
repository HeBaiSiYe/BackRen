package cn.edu.aynu.siyemanager.service.impl;

import cn.edu.aynu.siyemanager.dto.CertificateAddDTO;
import cn.edu.aynu.siyemanager.dto.CertificateQueryDTO;
import cn.edu.aynu.siyemanager.entity.Certificate;
import cn.edu.aynu.siyemanager.mapper.CertificateMapper;
import cn.edu.aynu.siyemanager.mapper.PersonMapper;
import cn.edu.aynu.siyemanager.service.CertificateService;
import cn.edu.aynu.siyemanager.vo.CertificateAddVO;
import cn.edu.aynu.siyemanager.vo.PageResult;
import cn.edu.aynu.siyemanager.vo.CertificateListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateMapper certificateMapper;

    @Autowired
    private PersonMapper personMapper;

    @Override
    public PageResult<CertificateListVO> getCertificateList(CertificateQueryDTO queryDTO) {
        // 设置默认值
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }

        // 计算offset
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);

        // 查询列表
        List<CertificateListVO> records = certificateMapper.selectCertificateList(queryDTO);

        // 查询总数
        long total = certificateMapper.countCertificateList(queryDTO);

        // 构建分页结果
        PageResult<CertificateListVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(queryDTO.getPage());
        result.setPageSize(queryDTO.getPageSize());

        return result;
    }

    @Override
    @Transactional
    public CertificateAddVO addCertificate(CertificateAddDTO addDTO) {
        // 1. 参数校验
        if (addDTO.getPersonId() == null) {
            throw new IllegalArgumentException("人员ID不能为空");
        }
        if (addDTO.getType() == null) {
            throw new IllegalArgumentException("证件类型不能为空");
        }
        if (addDTO.getNumber() == null || addDTO.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("证件号码不能为空");
        }
        if (addDTO.getValidYears() == null || addDTO.getValidYears() <= 0) {
            throw new IllegalArgumentException("有效年数必须大于0");
        }

        // 2. 创建证件对象
        Certificate certificate = new Certificate();
        certificate.setPersonId(addDTO.getPersonId());
        certificate.setType(addDTO.getType());
        certificate.setNumber(addDTO.getNumber().trim());

        // 3. 设置日期
        LocalDate now = LocalDate.now();
        certificate.setIssueDate(now);
        certificate.setExpireDate(now.plusYears(addDTO.getValidYears()));

        // 4. 设置状态
        certificate.setStatus(0); // 办理中

        // 5. 设置创建时间
        certificate.setCreateTime(LocalDateTime.now());

        // 6. 插入数据
        int result = certificateMapper.insert(certificate);
        if (result <= 0) {
            throw new RuntimeException("证件办理申请提交失败");
        }

        // 7. 新增：如果是身份证，将该人员的id_card设置为这个身份证号码
        if (certificate.getType() == 1) {
            // 更新人员身份证号
            personMapper.updateIdCard(certificate.getPersonId(), certificate.getNumber());
        }

        // 8. 构建返回VO
        CertificateAddVO vo = new CertificateAddVO();
        BeanUtils.copyProperties(certificate, vo);

        // 9. 设置类型名称
        String typeName = "";
        if (certificate.getType() == 1) {
            typeName = "身份证";
        } else if (certificate.getType() == 2) {
            typeName = "居住证";
        }
        vo.setTypeName(typeName);

        // 10. 设置状态名称
        vo.setStatusName("办理中");

        return vo;
    }

    @Override
    @Transactional
    public boolean cancelCertificate(Long id) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("证件ID不能为空");
        }

        // 2. 查询证件信息
        Certificate certificate = certificateMapper.selectById(id);
        if (certificate == null) {
            return false;
        }

        // 3. 检查是否已注销
        if (certificate.getStatus() == 5) {
            throw new RuntimeException("证件已注销，无法重复操作");
        }

        // 4. 更新证件状态为已注销
        int result = certificateMapper.updateStatus(id, 5);
        if (result <= 0) {
            return false;
        }

        // 5. 如果是身份证，将对应人员的id_card设置为NULL
        if (certificate.getType() == 1) {
            // 根据人员ID清空身份证号
            personMapper.clearIdCard(certificate.getPersonId());
        }

        return true;
    }

    @Override
    @Transactional
    public boolean reviewCertificate(Long id, Boolean approved) {
        // 1. 查询证件
        Certificate certificate = certificateMapper.selectById(id);
        if (certificate == null) {
            return false;
        }

        // 2. 检查状态是否为办理中(0)
        if (certificate.getStatus() != 0) {
            throw new RuntimeException("证件状态不合法，无法审核");
        }

        // 3. 根据审核结果处理
        if (approved) {
            // 通过：状态改为1(有效)
            certificateMapper.updateStatus(id, 1);

            // 如果是身份证，更新人员身份证号
            if (certificate.getType() == 1) {
                personMapper.updateIdCard(certificate.getPersonId(), certificate.getNumber());
            }
        } else {
            // 拒绝：状态改为4(已拒绝)
            certificateMapper.updateStatus(id, 4);

            // 如果是身份证，清空人员身份证号
            if (certificate.getType() == 1) {
                personMapper.clearIdCard(certificate.getPersonId());
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updateCertificateStatus(Long id, Integer status) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("证件ID不能为空");
        }
        if (status == null || status < 0 || status > 5) {
            throw new IllegalArgumentException("状态码无效，必须是0-5之间的整数");
        }

        // 2. 查询证件信息
        Certificate certificate = certificateMapper.selectById(id);
        if (certificate == null) {
            return false;
        }

        // 3. 更新证件状态
        int result = certificateMapper.updateStatus(id, status);
        if (result <= 0) {
            return false;
        }

        // 4. 特殊处理：身份证状态变更同步人员表
        if (certificate.getType() == 1) {
            if (status == 5) { // 已注销
                personMapper.clearIdCard(certificate.getPersonId());
            } else if (status == 1) { // 有效
                personMapper.updateIdCard(certificate.getPersonId(), certificate.getNumber());
            }
        }

        return true;
    }
}