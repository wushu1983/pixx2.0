/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.pig4cloud.pigx.admin.config;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.plugins.parser.ISqlParser;
import com.baomidou.mybatisplus.plugins.parser.tenant.TenantHandler;
import com.baomidou.mybatisplus.plugins.parser.tenant.TenantSqlParser;
import com.pig4cloud.pigx.common.core.datascope.DataScopeInterceptor;
import com.pig4cloud.pigx.common.core.util.TenantUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lengleng
 * @date 2017/10/29
 */
@Slf4j
@Configuration
@MapperScan("com.pig4cloud.pigx.admin.mapper")
public class MybatisPlusConfigurer {
	private static final String[] IGNORETABLES = new String[]{"sys_dept_relation", "sys_role_dept", "sys_role_menu", "sys_user_role"};

	/**
	 * 分页插件
	 *
	 * @return PaginationInterceptor
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		List<ISqlParser> sqlParserList = new ArrayList<>();
		TenantSqlParser tenantSqlParser = new TenantSqlParser();
		tenantSqlParser.setTenantHandler(new TenantHandler() {
			@Override
			public Expression getTenantId() {
				Integer tenantId = TenantUtils.getTenantId();
				log.debug("当前租户为 >> {}", tenantId);
				return new LongValue(tenantId);
			}

			@Override
			public String getTenantIdColumn() {
				return "tenant_id";
			}

			@Override
			public boolean doTableFilter(String tableName) {
				return ArrayUtil.contains(IGNORETABLES, tableName);
			}
		});
		sqlParserList.add(tenantSqlParser);
		paginationInterceptor.setSqlParserList(sqlParserList);
		return paginationInterceptor;
	}

	/**
	 * 数据权限插件
	 *
	 * @return DataScopeInterceptor
	 */
	@Bean
	public DataScopeInterceptor dataScopeInterceptor() {
		return new DataScopeInterceptor();
	}
}
