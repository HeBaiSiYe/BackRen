package cn.edu.aynu.siyemanager.service.impl;

import cn.edu.aynu.siyemanager.mapper.HouseholdMapper;
import cn.edu.aynu.siyemanager.mapper.PersonMapper;
import cn.edu.aynu.siyemanager.mapper.CertificateMapper;
import cn.edu.aynu.siyemanager.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private HouseholdMapper householdMapper;

    @Autowired
    private CertificateMapper certificateMapper;

    @Override
    public long getPersonCount() {
        return personMapper.countAll();
    }

    @Override
    public long getHouseholdCount() {
        return householdMapper.countAll();
    }

    @Override
    public long getCertificateCount() {
        return certificateMapper.countAll();
    }

    @Override
    public long getProcessingCertificateCount() {
        return certificateMapper.countByStatus(0);
    }
}