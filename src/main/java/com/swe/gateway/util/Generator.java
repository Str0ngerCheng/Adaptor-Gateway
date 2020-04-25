package com.swe.gateway.util;



import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author cbw
 */
public class Generator {

    public static void main(String[] args) {
        Generator.generateByTables (false,"com.swe.gateway"
                ,"cbw", "adapter","observation");

    }

    /**
     * @param serviceNameStartWithI
     * @param packageName   包名
     * @param author  作者
     * @param database  数据库名
     * @param tableNames 表名
     */
    private static void generateByTables(boolean serviceNameStartWithI, String packageName, String author, String database, String... tableNames) {

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 选择 freemarker 引擎，默认 Veloctiy
       // mpg.setTemplateEngine(new FreemarkerTemplateEngine ());

        GlobalConfig config = new GlobalConfig();
        config.setBaseColumnList(true);				//开启 baseColumnList 默认false
        config.setBaseResultMap(true);				//开启 BaseResultMap 默认false

        String dbUrl = "jdbc:mysql://175.24.66.249:3306/" + database + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true";
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setUrl(dbUrl)
                .setUsername("root")
                .setPassword("swe123,./")
                .setDriverName("com.mysql.cj.jdbc.Driver");
        StrategyConfig strategyConfig = new StrategyConfig();



        strategyConfig
                .setCapitalMode(true)
                .setDbColumnUnderline(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .setInclude(tableNames)//修改替换成你需要的表名，多个表名传数组
                .entityTableFieldAnnotationEnable(true)
                .setRestControllerStyle(true)
                .setEntityLombokModel(false)
                .setLogicDeleteFieldName("deleted");



        config.setActiveRecord(false)
                .setAuthor(author)
                .setOutputDir("E:\\学习\\SensorWeb\\swe_project\\Adaptor-Gateway\\src\\main\\java")
                .setFileOverride(true)
                .setEnableCache(false);

        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setEntity("./template/entity.java.vm")
                .setMapper("./template/mapper.java.vm")
                .setXml("./template/mapper.xml.vm");


        mpg.setGlobalConfig(config)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setTemplate(templateConfig)
                .setPackageInfo(
                        new PackageConfig ()
                                .setParent(packageName)
                                .setEntity("model")
                                .setMapper("dao")
                                .setXml("dao")
                ).execute();
    }

}
