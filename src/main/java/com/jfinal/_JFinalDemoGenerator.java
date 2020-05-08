package com.jfinal;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.GeneratorConfig;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;

/**
 * 代码生成demo
 */
public class _JFinalDemoGenerator {

	private static final String url = "jdbc:mysql://mysql:3306/ambow_order?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
	private static final String user = "ambow_order";
	private static final String pwd = "Ne6OFeThTQmh3hYR";
	// 是否第一次生成,第一次生成项目需要的全部文件
	private static final boolean genFirst = true;

	private static final String author = "ghy";
	private static final String apiPrefix = "/api/v2";
	private static final String projectName = "order";
	private static final String port = "30000";
	private static final String serverName = "统计服务";
	private static final String outPath = "E:/gen";

	// 生成tables 空为全部生成
	private static final String[] tables = {};


	public static void main(String[] args) {
		GeneratorConfig config = new GeneratorConfig(projectName,port,author,serverName,outPath);

		// 创建生成器
		Generator generator = new Generator(url, user, pwd, config, genFirst);

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




