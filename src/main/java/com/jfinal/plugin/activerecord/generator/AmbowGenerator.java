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

import com.jfinal.kit.JavaKeyword;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;

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
	protected String controllerTemplate = "/com/jfinal/plugin/activerecord/generator/controller_template.jf";
	protected String serviceTemplate = "/com/jfinal/plugin/activerecord/generator/service_template.jf";
	protected String serviceImplTemplate = "/com/jfinal/plugin/activerecord/generator/serviceimpl_template.jf";
	protected String sqlTemplate = "/com/jfinal/plugin/activerecord/generator/sql_template.jf";


	private String controllerPackageName;
	private String servicePackageName;
	private String serviceImplPackageName;
	protected String modelPackageName;
	protected String resourceDir;
	protected String controllerOutputDir;
	protected String serviceOutputDir;
	protected String serviceImplOutputDir;
	protected boolean generateChainSetter = false;


	protected JavaKeyword javaKeyword = JavaKeyword.me;

	protected boolean generateController = true;
	protected boolean generateService = true;

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

	public AmbowGenerator(String basePackageName, String baseOutputDir,String resourceDir) {
		if (StrKit.isBlank(basePackageName)) {
			throw new IllegalArgumentException("baseModelPackageName can not be blank.");
		}
		if (basePackageName.contains("/") || basePackageName.contains("\\")) {
			throw new IllegalArgumentException("baseModelPackageName error : " + basePackageName);
		}
		if (StrKit.isBlank(baseOutputDir)) {
			throw new IllegalArgumentException("baseModelOutputDir can not be blank.");
		}
		this.controllerPackageName = basePackageName + "." + "controller";
		this.servicePackageName = basePackageName + "." + "service";
		this.serviceImplPackageName = basePackageName + "." + "service.impl";
		this.controllerOutputDir = baseOutputDir + "/controller";
		this.serviceOutputDir = baseOutputDir + "/service";
		this.serviceImplOutputDir = baseOutputDir + "/service/impl";
		this.resourceDir = resourceDir;
		initEngine();
	}

	public AmbowGenerator(String basePackageName, String baseOutputDir,String resourceDir,boolean generateController,boolean generateService) {
		if (StrKit.isBlank(basePackageName)) {
			throw new IllegalArgumentException("baseModelPackageName can not be blank.");
		}
		if (basePackageName.contains("/") || basePackageName.contains("\\")) {
			throw new IllegalArgumentException("baseModelPackageName error : " + basePackageName);
		}
		if (StrKit.isBlank(baseOutputDir)) {
			throw new IllegalArgumentException("baseModelOutputDir can not be blank.");
		}
		this.controllerPackageName = basePackageName + "." + "controller";
		this.servicePackageName = basePackageName + "." + "service";
		this.serviceImplPackageName = basePackageName + "." + "service.impl";
		this.controllerOutputDir = baseOutputDir + "/controller";
		this.serviceOutputDir = baseOutputDir + "/service";
		this.serviceImplOutputDir = baseOutputDir + "/service/impl";
		this.resourceDir = resourceDir;
		this.generateController = generateController;
		this.generateService = generateService;
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
		System.out.println("Generate base model ...");
		this.modelPackageName = modelPackageName;

		if(generateController){
			for (TableMeta tableMeta : tableMetas) {
				genControllerContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,controllerOutputDir,"","Controller");
		}

		if(generateService){
			for (TableMeta tableMeta : tableMetas) {
				genServiceContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,serviceOutputDir,"I","Service");

			for (TableMeta tableMeta : tableMetas) {
				genServiceImplContent(tableMeta);
			}
			writeToFileNotRep(tableMetas,serviceImplOutputDir,"","ServiceImpl");
		}



		for (TableMeta tableMeta : tableMetas) {
			genSqlContent(tableMeta);
		}
		writeToFile(tableMetas,resourceDir,"",".sql");
	}
	
	protected void genControllerContent(TableMeta tableMeta) {
		Kv data = Kv.by("controllerPackageName", controllerPackageName);
		data.set("generateChainSetter", generateChainSetter);
		data.set("tableMeta", tableMeta);
		data.set("serviceName","I".concat(tableMeta.modelName).concat("Service"));
		data.set("modelPackageName",modelPackageName);
		data.set("serviceImport",servicePackageName + ".I" + tableMeta.modelName + "Service");
		tableMeta.baseModelContent = engine.getTemplate(controllerTemplate).renderToString(data);
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
		data.set("generateChainSetter", generateChainSetter);
		data.set("tableMeta", tableMeta);
		data.set("modelPackageName",modelPackageName);
		data.set("serviceImport",servicePackageName + ".I" + tableMeta.modelName + "Service");
		data.set("serviceImplName",tableMeta.modelName + "ServiceImpl");
		data.set("serviceName","I" + tableMeta.modelName + "Service");

		tableMeta.baseModelContent = engine.getTemplate(serviceImplTemplate).renderToString(data);
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

		String target = outputDir + File.separator + prefix + tableMeta.modelName + subfix + ".java";

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

}