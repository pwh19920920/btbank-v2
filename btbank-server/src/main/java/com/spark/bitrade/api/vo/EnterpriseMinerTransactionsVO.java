package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import lombok.Data;

import java.util.List;

/**
 * EnterpriseMinerTransactionsVO
 *
 * @author biu
 * @since 2019/12/24 11:59
 */
@Data
public class EnterpriseMinerTransactionsVO {

    private List<EnterpriseMinerTransaction> content;
    private Long totalElements;
}
