package com.fan.extractor


fun main() {
    println(
        DefaultAccountingFirmNameExtractor.extractAccountingFirmName(
            """
   证券代码：002460    证券简称：赣锋锂业    编号：临2024-021
              江西赣锋锂业集团股份有限公司

      关于续聘2024年度会计师事务所及确定报酬的公告

    本公司及董事会全体成员保证公告内容的真实、准确和完整，
没有虚假记载、误导性陈述或重大遗漏。

    江西赣锋锂业集团股份有限公司（以下简称“公司”）于2024年3月28日召开的第五届董事会第七十五次会议审议通过了《关于续聘2024年度会计师事务所的议案》及《关于预计2024年度会计师事务所报酬的议案》，董事会同意续聘安永华明会计师事务所（特殊普通合伙）（以下简称“安永华明”）为公司2024年度境内财务报告及内部控制审计机构，续聘安永会计师事务所（以下简称“安永”）为公司2024年度境外财务报告审计机构。本事项尚需提交公司股东大会审议。其中，续聘境内会计师事务所情况如下：

    一、拟续聘会计师事务所的基本信息

    （一）机构信息

    1、基本信息

    安永华明于1992年9月成立，2012年8月完成本土化转制，从一家中外合作的有限责任制事务所转制为特殊普通合伙制事务所。安永华明总部设在北京，注册地址为北京市东城区东长安街1号东方广场安永大楼17层01-12室。截至2023年末拥有合伙人245人，首席合伙人为毛鞍宁先生。安永华明一直以来注重人才培养，截至2023年末拥有执业注册会计师近1800人，其中拥有证券相关业务服务经验的执业注册会计师超过1500人, 注册会计师中签署过证券服务业务审计报告的注册会计师近500人。安永华明2022年度业务总收入人民币59.06亿元，
        """.trimIndent()
        )
    )
}