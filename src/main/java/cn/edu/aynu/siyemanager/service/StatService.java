package cn.edu.aynu.siyemanager.service;

public interface StatService {

    /**
     * 获取人员总数
     */
    long getPersonCount();

    /**
     * 获取户籍总数
     */
    long getHouseholdCount();

    /**
     * 获取证件总数
     */
    long getCertificateCount();

    /**
     * 获取办理中证件总数
     */
    long getProcessingCertificateCount();
}