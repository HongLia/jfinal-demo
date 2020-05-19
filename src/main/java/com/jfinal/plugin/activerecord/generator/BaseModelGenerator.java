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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.jfinal.kit.JavaKeyword;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;

/**
 * Base model 生成器
 */
public class BaseModelGenerator {
	
	protected Engine engine;
	protected String template = "/com/jfinal/plugin/activerecord/generator/template/jf_model.jf";
	
	protected String baseModelPackageName;
	protected String baseModelOutputDir;
	protected boolean generateChainSetter = false;
	private boolean genModelColunmsName;
	protected JavaKeyword javaKeyword = JavaKeyword.me;
	

	
	public BaseModelGenerator(String baseModelPackageName, GeneratorConfig config) {
		if (StrKit.isBlank(baseModelPackageName)) {
			throw new IllegalArgumentException("baseModelPackageName can not be blank.");
		}
		if (baseModelPackageName.contains("/") || baseModelPackageName.contains("\\")) {
			throw new IllegalArgumentException("baseModelPackageName error : " + baseModelPackageName);
		}
		
		this.baseModelPackageName = baseModelPackageName;
		this.baseModelOutputDir = config.modelDir + "/src/main/java/com/ambow/" + config.projectName + "/entity/";

		initEngine();
	}
	
	protected void initEngine() {
		engine = new Engine();
		engine.setToClassPathSourceFactory();	// 从 class path 内读模板文件
		engine.addSharedMethod(new StrKit());
		engine.addSharedObject("javaKeyword", javaKeyword);
	}
	
	/**
	 * 使用自定义模板生成 base model
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setGenerateChainSetter(boolean generateChainSetter) {
		this.generateChainSetter = generateChainSetter;
	}
	
	public void generate(List<TableMeta> tableMetas, boolean genModelColunmsName) {
		System.out.println("Generate base model ...");
		System.out.println("Base Model Output Dir: " + baseModelOutputDir);
		this.genModelColunmsName = genModelColunmsName;
		for (TableMeta tableMeta : tableMetas) {
			genBaseModelContent(tableMeta);
		}
		writeToFile(tableMetas);
	}
	
	protected void genBaseModelContent(TableMeta tableMeta) {
		Kv data = Kv.by("baseModelPackageName", baseModelPackageName);
		data.set("generateChainSetter", generateChainSetter);
		data.set("tableMeta", tableMeta);
		data.set("genModelColunmsName", genModelColunmsName);
		tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
	}
	
	protected void writeToFile(List<TableMeta> tableMetas) {
		try {
			for (TableMeta tableMeta : tableMetas) {
				writeToFile(tableMeta);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * base model 覆盖写入
	 */
	protected void writeToFile(TableMeta tableMeta) throws IOException {
		File dir = new File(baseModelOutputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		String target = baseModelOutputDir + File.separator + tableMeta.baseModelName + ".java";
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(target), "UTF-8");
			osw.write(tableMeta.baseModelContent);
		}
		finally {
			if (osw != null) {
				osw.close();
			}
		}
	}
	
	public String getBaseModelPackageName() {
		return baseModelPackageName;
	}
	
	public String getBaseModelOutputDir() {
		return baseModelOutputDir;
	}
}






