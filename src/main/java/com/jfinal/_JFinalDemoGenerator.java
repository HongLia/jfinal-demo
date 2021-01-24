package com.jfinal;

import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.GeneratorConfig;

/**
 * 代码生成demo
 *  找不到包引入
 * 	<groupId>com.jfinal</groupId>
 * 	<artifactId>jfinal-saas</artifactId>
 * 	<version>1.0-SNAPSHOT</version>
 */
public class _JFinalDemoGenerator {

	private static final String url = "jdbc:mysql://localhost:3306/zhl_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
	private static final String user = "root";
	private static final String pwd = "root";
	// 是否第一次生成,第一次生成项目需要的全部文件
	private static final boolean genFirst = true;

	// 是否生成数据库字段名常量
	private static final boolean genModelColunmsName = true;
	//作者名称
	private static final String author = "hongliang";
	private static final String apiPrefix = "/api/v2";
	//项目抿成
	private static final String projectName = "MyProject";
	private static final String port = "30000";
	//项目中文名称
	private static final String serverName = "订单服务";
	//文件输出路径
	private static final String outPath = "E:/gen";

	// 生成tables 空为全部生成
	private static final String[] tables = {};


	public static void main(String[] args) {
		GeneratorConfig config = new GeneratorConfig(projectName,port,author,serverName,outPath);

		// 创建生成器
		Generator generator = new Generator(url, user, pwd, config, genFirst, genModelColunmsName);

		// 配置是否生成备注
		generator.setGenerateRemarks(true);
		generator.setGeneratorTables(tables);

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




