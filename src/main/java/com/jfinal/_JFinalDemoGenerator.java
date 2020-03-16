package com.jfinal;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;

/**
 * 代码生成demo
 */
public class _JFinalDemoGenerator {

	private static final String url = "jdbc:mysql://10.10.113.65:3306/app_admin?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false";
	private static final String user = "root";
	private static final String pwd = "123.abc";

	public static DataSource getDataSource() {
		DruidPlugin druidPlugin = new DruidPlugin(url, user, pwd);
		druidPlugin.setInitialSize(1).setMinIdle(1).setMaxActive(2000).setTimeBetweenEvictionRunsMillis(5000).setValidationQuery("select 1")
				.setTimeBetweenEvictionRunsMillis(60000).setMinEvictableIdleTimeMillis(30000).setFilters("stat,wall");
		druidPlugin.setConnectionProperties("useInformationSchema=true;remarks=true");
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.ambow.service.gen.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/ambow/service/gen/model/base";

		// controller所使用的包名
		String genPackageName = "com.ambow.servicegen";
		// controller文件保存路径
		String genOupputDir = PathKit.getWebRootPath() + "/src/main/java/com/ambow/service/gen";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.ambow.service.gen.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";

		String resourceDir = PathKit.getWebRootPath() + "/src/main/resources/template/gen";
		
		// 创建生成器
		Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir,genPackageName,genOupputDir,resourceDir,true,true);

		// 配置是否生成备注
		generator.setGenerateRemarks(true);
		
		// 设置数据库方言
		generator.setDialect(new MysqlDialect());
		
		// 设置是否生成链式 setter 方法
		generator.setGenerateChainSetter(true);
		
		// 添加不需要生成的表名
		generator.addExcludedTable("adv");
		
		// 设置是否在 Model 中生成 dao 对象
		generator.setGenerateDaoInModel(true);
		
		// 设置是否生成字典文件
		generator.setGenerateDataDictionary(true);
		
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		generator.setRemovedTableNamePrefixes("bus_");
		
		// 生成
		generator.generate();
	}
}




