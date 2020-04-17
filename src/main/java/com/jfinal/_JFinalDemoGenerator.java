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

	private static final String url = "jdbc:mysql://mysql:3306/ambow_student?serverTimezone=GMT%2B8&useSSL=false";
	private static final String user = "ambow_student";
	private static final String pwd = "GnGfHnbLKEnk6doG";
	private static final boolean controller = true;
	private static final boolean service = true;
	private static final String author = "ghy";
	private static final String apiPrefix = "/api/v2";

	public static DataSource getDataSource() {
		DruidPlugin druidPlugin = new DruidPlugin(url, user, pwd);
		druidPlugin.setInitialSize(1).setMinIdle(1).setMaxActive(2000).setTimeBetweenEvictionRunsMillis(5000).setValidationQuery("select 1")
				.setTimeBetweenEvictionRunsMillis(60000).setMinEvictableIdleTimeMillis(30000).setFilters("stat,wall");
		druidPlugin.setConnectionProperties("useInformationSchema=true;remarks=true");
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// model 所使用的包名
		String modelPackage = "com.ambow.student.entity";

		String genPackageName = "com.ambow.student";

		// model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/ambow/student/entity";

		String genOupputDir = PathKit.getWebRootPath() + "/src/main/java/com/ambow/student";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.ambow.service.gen.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";

		String resourceDir = PathKit.getWebRootPath() + "/src/main/resources/template/gen";
		
		// 创建生成器
		Generator generator = new Generator(getDataSource(), modelPackage, baseModelOutputDir,genPackageName,genOupputDir,resourceDir, controller, service);

		// 配置是否生成备注
		generator.setGenerateRemarks(true);
		generator.setGeneratorTables(new String[]{"teacher"});

		generator.setAuthor(author);

		generator.setApiPrefix(apiPrefix);
		
		// 设置数据库方言
		generator.setDialect(new MysqlDialect());
		
		// 设置是否生成链式 setter 方法
		generator.setGenerateChainSetter(true);
		
		// 添加不需要生成的表名
		generator.addExcludedTable("adv");
		
		// 设置是否生成字典文件
		generator.setGenerateDataDictionary(true);
		
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		generator.setRemovedTableNamePrefixes("t_");
		
		// 生成
		generator.generate();
	}
}




