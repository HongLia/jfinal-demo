/**
 * Copyright (c) 2011-2019, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.plugin.activerecord.generator;

import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.JavaKeyword;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base model 生成器
 */
public class AmbowGenerator {

	protected Engine engine;
	protected String controllerTemplate = "/com/jfinal/plugin/activerecord/generator/template/controller_template.jf";
	protected String feignTemplate = "/com/jfinal/plugin/activerecord/generator/template/feign_template.jf";
	protected String feignImplTemplate = "/com/jfinal/plugin/activerecord/generator/template/feignimpl_template.jf";
	protected String serviceTemplate = "/com/jfinal/plugin/activerecord/generator/template/service_template.jf";
	protected String serviceImplTemplate = "/com/jfinal/plugin/activerecord/generator/template/serviceimpl_template.jf";
	protected String mapperTemplate = "/com/jfinal/plugin/activerecord/generator/template/mapper_template.jf";
	protected String mapperXmlTemplate = "/com/jfinal/plugin/activerecord/generator/template/jf_mapper_xml.jf";
	protected String sqlTemplate = "/com/jfinal/plugin/activerecord/generator/template/sql_template.jf";
	protected String irpcPomTemplate = "/com/jfinal/plugin/activerecord/generator/template/jf_irpc_pom.jf";
	protected String rpcPomTemplate = "/com/jfinal/plugin/activerecord/generator/template/jf_rpc_pom.jf";
	protected String modelTemplate = "/com/jfinal/plugin/activerecord/generator/template/jf_model_pom.jf";
	protected String jenkinsfileTemplate = "/com/jfinal/plugin/activerecord/generator/template/jenkinsfile.jf";
	protected String applicationTemplate = "/com/jfinal/plugin/activerecord/generator/template/application.yml.jf";
	protected String bootstrapTemplate = "/com/jfinal/plugin/activerecord/generator/template/bootstrap.yml.jf";
	protected String logTemplate = "/com/jfinal/plugin/activerecord/generator/template/logback-spring.xml.jf";
    protected String proTemplate = "/com/jfinal/plugin/activerecord/generator/template/jf_pro_pom.jf";

	protected String redisConfigTemplate = "/com/jfinal/plugin/activerecord/generator/template/redisConfig.jf";
	protected String redisManagerTemplate = "/com/jfinal/plugin/activerecord/generator/template/redisManager.jf";
	protected String mapperHelperTemplate = "/com/jfinal/plugin/activerecord/generator/template/mapperHelperConfig.jf";
	protected String swaggerHelperTemplate = "/com/jfinal/plugin/activerecord/generator/template/swagger.jf";
	protected String configTemplate = "/com/jfinal/plugin/activerecord/generator/template/config.jf";

	protected String mainTemplate = "/com/jfinal/plugin/activerecord/generator/template/application.jf";



	private String controllerPackageName;
	private String feignPackageName;
	private String feignImplPackageName;
	private String servicePackageName;
    private String mapperPackageName;
	private String serviceImplPackageName;
	protected String modelPackageName;
	protected String resourceDir;
	protected String controllerOutputDir;
	protected String feignOutputDir;
	protected String feignImplOutputDir;
	protected String serviceOutputDir;
	protected String serviceImplOutputDir;
    protected String mapperOutputDir;
	protected boolean generateChainSetter = false;
	String dbUser;
	String dbPwd;
	String dbUrl;
	private GeneratorConfig config;

	protected JavaKeyword javaKeyword = JavaKeyword.me;

	protected boolean genEntityOnly = false;

	/**
	 * 针对 Model 中七种可以自动转换类型的 getter 方法，调用其具有确定类型返回值的 getter 方法
	 * 享用自动类型转换的便利性，例如 getInt(String)、getStr(String)
	 * 其它方法使用泛型返回值方法： get(String)
	 * 注意：jfinal 3.2 及以上版本 Model 中的六种 getter 方法才具有类型转换功能
	 */
	@SuppressWarnings("serial")
	protected Map<String, String> getterTypeMap = new HashMap<String, String>() {{
		put("java.lang.String", "getStr");
		put("java.lang.Integer", "getInt");
		put("java.lang.Long", "getLong");
		put("java.lang.Double", "getDouble");
		put("java.lang.Float", "getFloat");
		put("java.lang.Short", "getShort");
		put("java.lang.Byte", "getByte");
	}};


	public AmbowGenerator(String dbUrl,String dbUser,String dbPwd,GeneratorConfig config,boolean genEntityOnly) {

		if (StrKit.isBlank(config.getBasePackage())) {
			throw new IllegalArgumentException("baseModelPackageName can not be blank.");
		}
		if (config.getBasePackage().contains("/") || config.getBasePackage().contains("\\")) {
			throw new IllegalArgumentException("baseModelPackageName error : " + config.getBasePackage());
		}
		this.config = config;
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPwd = dbPwd;
		this.controllerPackageName = config.getBasePackage() + "." + "controller";
		this.feignPackageName = config.getBasePackage() + "." + "feign";
		this.feignImplPackageName = config.getBasePackage() + "." + "feign.impl";
		this.servicePackageName = config.getBasePackage() + "." + "service";
		this.serviceImplPackageName = config.getBasePackage() + "." + "service.impl";
        this.mapperPackageName = config.getBasePackage() + ".mapper";
		this.controllerOutputDir = config.getRpcDir() + "/src/main/java/" + config.basePackageDir + "/controller";

		this.feignOutputDir = config.irpcDir + "/src/main/java/" + config.basePackageDir + "/feign";
		this.feignImplOutputDir = config.rpcDir + "/src/main/java/" + config.basePackageDir + "/feign/impl";
		this.serviceOutputDir = config.rpcDir + "/src/main/java/" + config.basePackageDir + "/service";
		this.serviceImplOutputDir = config.rpcDir + "/src/main/java/" + config.basePackageDir + "/service/impl";
        this.mapperOutputDir = config.rpcDir + "/src/main/java/" + config.basePackageDir + "/mapper";
		this.resourceDir = config.rpcDir + "/src/main/resources/";
		this.genEntityOnly = genEntityOnly;
		initEngine();
	}
	
	protected void initEngine() {
		engine = new Engine();
		engine.setToClassPathSourceFactory();	// 从 class path 内读模板文件
		engine.addSharedMethod(new StrKit());
		engine.addSharedObject("getterTypeMap", getterTypeMap);
		engine.addSharedObject("javaKeyword", javaKeyword);
	}


	public void setGenerateChainSetter(boolean generateChainSetter) {
		this.generateChainSetter = generateChainSetter;
	}
	
	public void generate(List<TableMeta> tableMetas,String modelPackageName) {
		System.out.println("Generate controller & service ...");
		this.modelPackageName = modelPackageName;

		if(!genEntityOnly){
			for (TableMeta tableMeta : tableMetas) {
				genControllerContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,controllerOutputDir,"","Controller");

			for (TableMeta tableMeta : tableMetas) {
				genFeignContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,feignOutputDir,"","FeignClient");
		}

		if(!genEntityOnly){
			for (TableMeta tableMeta : tableMetas) {
				genServiceContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,serviceOutputDir,"I","Service");

			for (TableMeta tableMeta : tableMetas) {
				genServiceImplContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,serviceImplOutputDir,"","ServiceImpl");

			for (TableMeta tableMeta : tableMetas) {
				genFeignImplContent(tableMeta);
			}
			writeToFileNotRep(tableMetas, feignImplOutputDir,"","FeignClientImpl");

			for (TableMeta tableMeta : tableMetas) {
				genMapperContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,mapperOutputDir,"","Mapper");

			for (TableMeta tableMeta : tableMetas) {
				genMapperXmlContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,resourceDir + "mapper/","","Mapper.xml");
			genOther(tableMetas.get(0));
		}
	}

	private void genOther(TableMeta tableMeta) {
		try {

			String mainClassName = StrUtil.upperFirst(config.projectName.split("-")[0] )+ "Application";
			genMainContent(tableMeta, mainTemplate,mainClassName);

			writeToFileNotRep(config.rpcDir + "/src/main/java/" + config.basePackageDir,
					mainClassName + ".java", tableMeta.baseModelContent);

			String configDir = "/src/main/java/" + config.basePackageDir + "/config";
			genConfigContent(tableMeta, redisConfigTemplate);
			writeToFileNotRep(config.rpcDir + configDir,"RedisConfig.java", tableMeta.baseModelContent);

			genConfigContent(tableMeta, redisManagerTemplate);
			writeToFileNotRep(config.rpcDir + configDir,"RedisManager.java", tableMeta.baseModelContent);

			genConfigContent(tableMeta, mapperHelperTemplate);
			writeToFileNotRep(config.rpcDir + configDir,"MapperHelperConfig.java", tableMeta.baseModelContent);

			genConfigContent(tableMeta, swaggerHelperTemplate);
			writeToFileNotRep(config.rpcDir + configDir,"SwaggerConfig.java", tableMeta.baseModelContent);


            genPomContent(tableMeta, proTemplate);
            writeToFileNotRep(config.baseDir,"pom.xml", tableMeta.baseModelContent);

			genPomContent(tableMeta, irpcPomTemplate);
			writeToFileNotRep(config.irpcDir,"pom.xml", tableMeta.baseModelContent);
			writeToFileNotRep(config.irpcDir,".gitignore",config.igno);

			genPomContent(tableMeta, modelTemplate);
			writeToFileNotRep(config.modelDir,"pom.xml", tableMeta.baseModelContent);
			writeToFileNotRep(config.modelDir,".gitignore",config.igno);

			genPomContent(tableMeta, rpcPomTemplate);
			writeToFileNotRep(config.rpcDir,"pom.xml", tableMeta.baseModelContent);
			writeToFileNotRep(config.rpcDir,".gitignore",config.igno);

			genPomContent(tableMeta, jenkinsfileTemplate);
			writeToFileNotRep(config.rpcDir,"jenkinsfile", tableMeta.baseModelContent);

			genPomContent(tableMeta, applicationTemplate);
			writeToFileNotRep(resourceDir,"application.yml", tableMeta.baseModelContent);


			genConfigYmlContent(tableMeta, configTemplate);
			writeToFileNotRep(resourceDir ,  config.projectName + "-local.yml", tableMeta.baseModelContent);
			writeToFileNotRep(resourceDir ,  config.projectName + "-fix.yml", tableMeta.baseModelContent);
			writeToFileNotRep(resourceDir ,  config.projectName + "-dev.yml", tableMeta.baseModelContent);
			writeToFileNotRep(resourceDir ,  config.projectName + "-uat.yml", tableMeta.baseModelContent);
			writeToFileNotRep(resourceDir ,  config.projectName + "-pre.yml", tableMeta.baseModelContent);
			writeToFileNotRep(resourceDir ,  config.projectName + "-prod.yml", tableMeta.baseModelContent);

			genPomContent(tableMeta, bootstrapTemplate);
			writeToFileNotRep(resourceDir,"bootstrap.yml", tableMeta.baseModelContent);

			genPomContent(tableMeta, logTemplate);
			writeToFileNotRep(resourceDir,"logback-spring.xml", tableMeta.baseModelContent);

//
//			genIrpcPomContent(tableMeta);
//			writeToFileNotRep(tableMeta, config.irpcDir,"","pom.xml");
//
//			genIrpcPomContent(tableMeta);
//			writeToFileNotRep(tableMeta, config.irpcDir,"","pom.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	protected void genControllerContent(TableMeta tableMeta) {
		Kv data = Kv.by("controllerPackageName", controllerPackageName);
		data.set("generateChainSetter", generateChainSetter);
		data.set("tableMeta", tableMeta);
		data.set("serviceName","I".concat(tableMeta.modelName).concat("Service"));
		data.set("modelPackageName",modelPackageName);
		data.set("serviceImport",servicePackageName + ".I" + tableMeta.modelName + "Service");
		data.set("stringpk","java.lang.String".equals(tableMeta.primaryKeyJavaType));
		if(StringUtils.hasText(tableMeta.apiPrefix)){
			if(!tableMeta.apiPrefix.startsWith("/"))
				tableMeta.apiPrefix = "/" + tableMeta.apiPrefix;
		}
		data.set("apiPrefix", tableMeta.apiPrefix);
		//data.set("idJavaType", );
		tableMeta.baseModelContent = engine.getTemplate(controllerTemplate).renderToString(data);
	}

	protected void genFeignContent(TableMeta tableMeta) {
		Kv data = Kv.by("feignPackageName", feignPackageName);
		data.set("tableMeta", tableMeta);
		data.set("modelPackageName",modelPackageName);
		if(StringUtils.hasText(tableMeta.apiPrefix)){
			if(!tableMeta.apiPrefix.startsWith("/"))
				tableMeta.apiPrefix = "/" + tableMeta.apiPrefix;
		}
		data.set("apiPrefix", tableMeta.apiPrefix);
		data.set("projectName", config.projectName);
		//data.set("idJavaType", );
		tableMeta.baseModelContent = engine.getTemplate(feignTemplate).renderToString(data);
	}

	protected void genServiceContent(TableMeta tableMeta) {
		Kv data = Kv.by("modelPackageName", modelPackageName);
		data.set("generateChainSetter", generateChainSetter);
		data.set("tableMeta", tableMeta);
		data.set("servicePackageName",servicePackageName);
		tableMeta.baseModelContent = engine.getTemplate(serviceTemplate).renderToString(data);
	}

	protected void genServiceImplContent(TableMeta tableMeta) {
		Kv data = Kv.by("serviceImplPackageName", serviceImplPackageName);
		data.set("tableMeta", tableMeta);
		data.set("modelPackageName",modelPackageName);
		data.set("serviceImport",servicePackageName + ".I" + tableMeta.modelName + "Service");
		data.set("serviceImplName",tableMeta.modelName + "ServiceImpl");
		data.set("serviceName","I" + tableMeta.modelName + "Service");
        data.set("mapperImport", mapperPackageName + "." + tableMeta.modelName + "Mapper");
		tableMeta.baseModelContent = engine.getTemplate(serviceImplTemplate).renderToString(data);
	}

	protected void genFeignImplContent(TableMeta tableMeta) {
		Kv data = Kv.by("feignImplPackageName", feignImplPackageName);
		data.set("tableMeta", tableMeta);
		data.set("modelPackageName",modelPackageName);
		data.set("serviceImport",servicePackageName + ".I" + tableMeta.modelName + "Service");
		data.set("serviceImplName",tableMeta.modelName + "ServiceImpl");
		data.set("serviceName","I" + tableMeta.modelName + "Service");
		data.set("feignPackageName",feignPackageName);
		data.set("stringpk","java.lang.String".equals(tableMeta.primaryKeyJavaType));
		tableMeta.baseModelContent = engine.getTemplate(feignImplTemplate).renderToString(data);
	}

	protected void genMapperContent(TableMeta tableMeta) {
		Kv data = Kv.by("mapperPackageName", mapperPackageName);
		data.set("tableMeta", tableMeta);
		data.set("modelPackageName",modelPackageName);
		tableMeta.baseModelContent = engine.getTemplate(mapperTemplate).renderToString(data);
	}

	protected void genMapperXmlContent(TableMeta tableMeta) {
		Kv data = Kv.by("mapperImport", mapperPackageName + "." + tableMeta.modelName + "Mapper");
		data.set("tableMeta", tableMeta);
		tableMeta.baseModelContent = engine.getTemplate(mapperXmlTemplate).renderToString(data);
	}


	protected void genPomContent(TableMeta tableMeta, String template) {
		Kv data = Kv.by("projectName", config.projectName);
		data.set("serverName", config.serverName);
		data.set("tableMeta", tableMeta);
		data.set("port", config.port);

		tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
	}

	protected void genConfigContent(TableMeta tableMeta, String template){
		Kv data = Kv.by("configPackageName", config.basePackage + ".config");
		data.set("serverName", config.serverName);
		tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
	}

	protected void genMainContent(TableMeta tableMeta, String template,String mainClassName){
		Kv data = Kv.by("appPackageName", config.basePackage);
		data.set("fProjectName", mainClassName);
		data.set("projectName", config.projectName);
		tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
	}


	protected void genConfigYmlContent(TableMeta tableMeta, String template) {
		Kv data = Kv.by("projectName", config.projectName);
		data.set("dburl", this.dbUrl);
		data.set("dbuser", this.dbUser);
		data.set("dbpwd", this.dbPwd);
		tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
	}


	protected void genSqlContent(TableMeta tableMeta) {
		String abbreviationUp = "";

		for (char ch : tableMeta.modelName.toCharArray()) {
			if(ch >= 'A' && ch <= 'Z'){
				abbreviationUp += ch;
			}
		}

		if("".equals(abbreviationUp))
			abbreviationUp = tableMeta.modelName;

		Kv data = Kv.by("tableMeta", tableMeta);
		data.set("generateChainSetter", generateChainSetter);
		data.set("abbreviationUp", abbreviationUp);
		data.set("abbreviationLow",abbreviationUp.toLowerCase());


		tableMeta.baseModelContent = engine.getTemplate(sqlTemplate).renderToString(data);
	}
	
	protected void writeToFile(List<TableMeta> tableMetas,String outputDir,String prefix,String subfix) {
		try {
			for (TableMeta tableMeta : tableMetas) {
				writeToFile(tableMeta,outputDir,prefix,subfix);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * base model 覆盖写入
	 */
	protected void writeToFile(TableMeta tableMeta,String outputDir,String prefix,String subfix) throws IOException {
		File dir = new File(outputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String target = null;
		if(subfix.endsWith(".sql")){
			target = outputDir + File.separator + prefix + tableMeta.modelName + subfix;
		}else
			target = outputDir + File.separator + prefix + tableMeta.modelName + subfix + ".java";

		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(target), "UTF-8");
			osw.write(tableMeta.baseModelContent);
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			if (osw != null) {
				osw.close();
			}
		}
	}

	/**
	 *
	 * @param tableMetas
	 * @param outputDir
	 * @param prefix
	 * @param subfix
	 */
	protected void writeToFileNotRep(List<TableMeta> tableMetas,String outputDir,String prefix,String subfix) {
		try {
			for (TableMeta tableMeta : tableMetas) {
				writeToFileNotRep(tableMeta,outputDir,prefix,subfix);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 若文件存在，则不生成，以免覆盖用户手写的代码
	 */
	protected void writeToFileNotRep(TableMeta tableMeta,String outputDir,String prefix,String subfix) throws IOException {
		File dir = new File(outputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String target = outputDir + File.separator + prefix + tableMeta.modelName + subfix + ((subfix.contains(".") ? "" : ".java"));

		File file = new File(target);
		if (file.exists()) {
			return ;
		}

		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			osw.write(tableMeta.baseModelContent);
		}
		finally {
			if (osw != null) {
				osw.close();
			}
		}
	}

	protected void writeToFileNotRep(String outputDir,String fileName,String bodyContent) throws IOException {
		File dir = new File(outputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String target = outputDir + File.separator + fileName;

		File file = new File(target);
		if (file.exists()) {
			return ;
		}

		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			osw.write(bodyContent);
		}
		finally {
			if (osw != null) {
				osw.close();
			}
		}
	}

}