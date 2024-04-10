//package com.fan.extractor
//
//
//fun main() {
//    println(
//        DefaultAccountingFirmNameExtractor.extractAccountingFirmName(
//            """
//
//证券代码：603032              证券简称：德新科技            公告编号：2024-019
//          德力西新能源科技股份有限公司
//
//          2024年度续聘会计师事务所的公告
//
//  本公司董事会及董事会全体成员保证信息披露的内容真实、准确、完整，没有虚假记载、误导性陈述或重大遗漏。
//
//    一、拟聘任会计师事务所的基本情况
//
//    （一）机构信息
//
//    1、基本信息
//
//    立信会计师事务所（特殊普通合伙）（以下简称“立信”）由我国会计泰
//斗潘序伦博士于1927年在上海创建，1986年复办，2010年成为全国首家完成改
//制的特殊普通合伙制会计师事务所，注册地址为上海市，首席合伙人为朱建弟
//先生。立信是国际会计网络BDO的成员所，长期从事证券服务业务，新证券法
//实施前具有证券、期货业务许可证，具有H股审计资格，并已向美国公众公司
//会计监督委员会（PCAOB）注册登记。
//
//    截至2023年末，立信拥有合伙人278名、注册会计师2,533名、从业人员总
//数10,730名，签署过证券服务业务审计报告的注册会计师693名。
//
//    立信2023年业务收入（未经审计）46.14亿元，其中审计业务收入34.08亿元，证券业务收入15.16亿元。
//
//    2023年度立信为671家上市公司提供年报审计服务，审计收费 8.17亿元，
//
//同行业上市公司审计客户28家。
//
//    2、投资者保护能力
//
//    截至2023年末，立信已提取职业风险基金1.61亿元，购买的职业保险累计
//赔偿限额为12.50亿元，相关职业保险能够覆盖因审计失败导致的民事赔偿责任。
//    近三年在执业行为相关民事诉讼中承担民事责任的情况：
//
//
// 起诉（仲                      诉讼（仲裁）    诉讼（仲
//
//            被诉（被仲裁）人                                      诉讼（仲裁）结果
//
//  裁）人                            事件        裁）金额
//
//                                                尚余1,000多  连带责任，立信投保的职业保险足
//
//            金亚科技、周旭
//
//  投资者                        2014年报        万，在诉讼  以覆盖赔偿金额，目前生效判决均
//
//            辉、立信
//
//                                                过程中      已履行
//
//                                                            一审判决立信对保千里在2016年12
//
//                                                            月30日至2017年12月14日期间因证
//
//            保千里、东北证    2015年重组、
//
//                                                            券虚假陈述行为对投资者所负债务
//
//  投资者    券、银信评估、立  2015年报、      80万元
//
//                                                            的15%承担补充赔偿责任，立信投
//
//            信等              2016年报
//
//                                                            保的职业保险12.5亿元足以覆盖赔
//
//                                                            偿金额
//
//    3、诚信记录
//
//    立信近三年因执业行为受到刑事处罚无、行政处罚1次、监督管理措施29次、自律监管措施1次和纪律处分无，涉及从业人员75名。
//
//    （二）项目信息
//
//    1、基本信息
//
//                              注册会计师执    开始从事上市  开始在本所执  开始为本公司提
//
//    项目          姓名
//
//                                业时间      公司审计时间      业时间      供审计服务时间
//
//项目合伙人      孙峰        2008年          2007年          2008年        2022年
//
//签字注册会计师  强爱斌      2013年          2011年          2013年        2022年
//
//质量控制复核人  魏琴        2005年          2007年          2005年        2022年
//
//    （1）项目合伙人近三年从业情况：
//
//    姓名：孙峰
//
//      时间                      上市公司名称                                职务
//
//2021-2023年      浙江田中精机股份有限公司                    签字合伙人
//
//2022-2023年      上海岱美汽车内饰件股份有限公司              签字合伙人
//
//2022-2023年      苏州春秋电子科技股份有限公司                签字合伙人
//
//2022-2023年      德力西新能源科技股份有限公司                签字合伙人
//
//2023年            宁波卡倍亿电气技术股份有限公司              签字合伙人
//
//2023年            普冉半导体（上海）股份有限公司              质量控制复核人
//
//
//  （2）签字注册会计师近三年从业情况：
//
//  姓名：强爱斌
//
//      时间                      上市公司名称                            职务
//
//2021-2022年        杭州大地海洋环保股份有限公司                签字会计师
//
//2021-2023年        德力西新能源科技股份有限公司                签字会计师
//
//2021年            宁波德业科技集团股份公司                    签字会计师
//
//2022年            江苏迈信林航空科技股份有限公司              签字会计师
//
//  （3）质量控制复核人近三年从业情况：
//
//  姓名：魏琴
//
//      时间                    上市公司名称                            职务
//
//2021-2023年        迪安诊断技术集团股份有限公司            签字合伙人
//
//2021-2023年        浙江大东南股份有限公司                  签字合伙人
//
//2021-2022年        江苏中信博新能源科技股份有限公司        签字合伙人
//
//2022-2023年        国邦医药集团股份有限公司                签字合伙人
//
//2023年            常州聚和新材料股份有限公司              签字合伙人
//
//2023年            欣灵电气股份有限公司                    签字合伙人
//
//2022-2023年        浙江爱仕达电器股份有限公司              质量控制复核人
//
//2022-2023年        贝达药业股份有限公司                    质量控制复核人
//
//2023年            江苏迈信林航空科技股份有限公司          质量控制复核人
//
//2023年            浙江田中精机股份有限公司                质量控制复核人
//
//  2、项目组成员独立性和诚信记录情况。
//
//  项目合伙人、签字注册会计师和质量控制复核人不存在违反《中国注册会计师职业道德守则》对独立性要求的情形。
//
//  （上述人员过去三年没有不良记录。）
//
//    二、审计收费
//
//  审计费用定价原则：2023 年度主要基于专业服务所承担的责任和需投入专业技术的程度，综合考虑参与工作员工的经验和级别相应的收费率以及投入的工作时间等因素定价。
//
//
//  公司 2023 年度审计费用为 160万元（其中财务报表审计费用为 130 万元，
//内控审计费用为 30 万元），定价原则未发生变化。公司授权管理层根据公司审计的具体工作量及市场价格水平决定 2024年审计费用。
//
//    三、拟续聘会计师事务所履行的程序
//
//  （一）董事会审计与风险控制委员会意见
//
//  2024年4月7日公司召开2024年第二次审计与风险控制委员会，会议审议通过了《关于续聘会计师事务所的议案》。审计与风险控制委员会认真审核了立信有关资料，并对其以前年度审计工作开展情况进行了适当的考察和评估，认为立信具备为上市公司提供审计服务的经验、专业胜任能力、投资者保护能力和良好的诚信状况，不存在违反《中国注册会计师职业道德守则》对独立性要求的情形。在担任公司2023年度财务审计及内控审计机构期间，严格遵守了国家有关法律法规及注册会计师职业规范的要求，出具的审计报告客观、公正、公允地反映了公司财务状况、经营成果及内控情况，切实履行了财务审计及内控审计机构应尽的职责。同意继续聘任立信为公司2024年度财务审计及内部控制审计机构，并将该议案提交公司董事会审议。
//
//  （二）公司于2024年4月8日召开第四届董事会第二十一次会议及第四届监事会第十七次会议，审议通过了《关于续聘会计师事务所的议案》，同意续聘立信为公司2024年度财务审计、内控审计机构。
//
//  （三）本次续聘会计师事务所事项尚需提交公司股东大会审议，并自公司2023年年度股东大会审议通过之日起生效。
//
//  特此公告。
//
//                                  德力西新能源科技股份有限公司董事会
//                                                        2024年4月10日
//
//        """.trimIndent()
//        )
//    )
//}