
import java.io.InputStream;

import org.junit.Test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author chennieyun
 */
public class TestConfigFile {
	
	//TODO:这个单元测试的意义在哪？觉得没有用，建议删除（lifw555@gmail.com）
    @Test
    public void testTheConfigFile() {
        String configFileName = "/org/fireflow/engine/kenelextensions/kenel-config.xml";
        InputStream inStream = this.getClass().getResourceAsStream(configFileName);
        if (inStream == null) {
            System.out.println("classpath中没有找到名称为" + configFileName + "的配置文件，无法初始化RuntimeContext。");
        } else {
            System.out.println("Find the ConfigFile");
        }
    }
}
