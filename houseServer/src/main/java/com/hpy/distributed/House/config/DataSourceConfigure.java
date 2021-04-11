package com.hpy.distributed.House.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author: beichenhpy
 * @Date: 2020/3/10 16:22
 */

@Configuration
public class DataSourceConfigure {

    @Bean
    @Primary
    public DataSourceProxy dataSourceProxy(DataSourceProperties properties){
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        return new DataSourceProxy(dataSource);
    }
}