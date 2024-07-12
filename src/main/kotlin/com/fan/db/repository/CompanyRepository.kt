package com.fan.db.repository

import com.fan.db.entity.Company
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
    fun findByStock(stock: String): Company?

    @Query(
        value =
            "SELECT c.stock, c.company_name, n.title, n.content " +
                "FROM company c, " +
                "notice_detail n " +
                "WHERE  n.stock = c.stock " +
                "AND c.stock NOT IN (SELECT r.stock FROM result r WHERE r.\"year\" = ?1)",
        nativeQuery = true,
    )
    fun findCompanyNoticeDetails(
        year: String,
        pageable: Pageable,
    ): Page<Any>

    @Query(
        value = """
    SELECT 
        C.stock ,
        C.company_name,
        COUNT(N.stock) AS count,
        N."year" as "YEAR"
    FROM 
        company C
    LEFT JOIN 
        search_by_code_source N ON C.stock = N.stock
    WHERE 
        N."year" is not null
    GROUP BY 
        C.stock, C.company_name, N."year"
    ORDER BY N."year" DESC, C.stock ASC
    """,
        nativeQuery = true,
    )
    fun findCompanyNotice(pageable: Pageable): Page<Map<String, String>>
}
