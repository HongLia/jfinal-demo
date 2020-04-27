package com.jfinal.plugin.activerecord.generator;

/**
 * 生成配置
 */

public class GeneratorConfig {

    public String projectName = "template";
    public String port = "8100";
    public String baseDir = "E:/gen/" + this.getProjectName();
    public String basePackage = "com.ambow." + projectName;
    public String basePackageDir = basePackage.replaceAll("\\.","/");
    public String irpcDir = baseDir + "/" + projectName + "-irpc" ;
    public String modelDir = baseDir + "/" + projectName + "-model";
    public String rpcDir = baseDir + "/" + projectName + "-rpc";
    public String author = "ghy";
    public String serverName = "字典服务";

    private GeneratorConfig config;
    public final String igno = "#idea忽略文件\n" +
            "/.idea/*\n" +
            "\n" +
            "#eclipse文件\n" +
            "/.settings/\n" +
            "/target/\n" +
            "/.apt_generated/\n" +
            ".classpath\n" +
            ".project\n" +
            ".factorypath\n" +
            "*.iml";
    private GeneratorConfig(){}
    public GeneratorConfig getDefault() {
        if(null == config){
            config = new GeneratorConfig();
            config.projectName = "template";
            config.port = "8100";
            config.baseDir = "E:/gen/" + projectName;
            config.basePackage = "com.ambow." + projectName;
            config.basePackageDir = basePackage.replaceAll("\\.","/");
            config.irpcDir = baseDir + "/" + projectName + "-irpc" ;
            config.modelDir = baseDir + "/" + projectName + "-model";
            config.rpcDir = baseDir + "/" + projectName + "-rpc";
            config.author = "ghy";
            config.serverName = "服务模板";
        }
        return config;
    }

    public GeneratorConfig(String projectName, String port, String author,String serverName, String outPath){
        this.projectName = projectName;
        this.port = port;
        this.baseDir = outPath + (outPath.endsWith("/") || outPath.endsWith("\\") ? "" : "/") + projectName;
        this.basePackage = "com.ambow." + projectName;
        this.basePackageDir = basePackage.replaceAll("\\.","/");
        this.irpcDir = baseDir + "/" + projectName + "-irpc" ;
        this.modelDir = baseDir + "/" + projectName + "-model";
        this.rpcDir = baseDir + "/" + projectName + "-rpc";
        this.author = author;
        this.serverName = serverName;
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getBasePackageDir() {
        return basePackageDir;
    }

    public void setBasePackageDir(String basePackageDir) {
        this.basePackageDir = basePackageDir;
    }

    public String getIrpcDir() {
        return irpcDir;
    }

    public void setIrpcDir(String irpcDir) {
        this.irpcDir = irpcDir;
    }

    public String getModelDir() {
        return modelDir;
    }

    public void setModelDir(String modelDir) {
        this.modelDir = modelDir;
    }

    public String getRpcDir() {
        return rpcDir;
    }

    public void setRpcDir(String rpcDir) {
        this.rpcDir = rpcDir;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
