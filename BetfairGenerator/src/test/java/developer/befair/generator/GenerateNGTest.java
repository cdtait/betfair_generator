/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package developer.befair.generator;

import developer.betfair.generator.Interface;
import developer.betfair.generator.RGenerator;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.File;

/**
 *
 * @author obod
 */
public class GenerateNGTest {
    
    public GenerateNGTest() {
    }

    /**
     * Clean up test directory and generated R file
     */
    public void cleanTestRDir() {
        File file = new File("TestR/");
        if (file.exists() && file.canWrite()) {
            File[] listFiles = file.listFiles();
            for(File f : listFiles) {
                f.delete();
            }
            file.delete();
        }
    }
    
    /**
     * Test if we can  generate from xml and create a reasonable size file.
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void genRSports() throws IOException, JAXBException {
        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Interface.class.getPackage().getName());
        javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
        Interface xmlInterface = (Interface) unmarshaller.unmarshal(new java.io.File("xml-resources/SportsAPING.xml")); //NOI18N
    
        assertNotNull(new RGenerator(xmlInterface,"TestR/","betting"));
        File file = new File("TestR/SportsAPING.R");
        // Should be over 162K bytes but its difficult to assess anything else about the genration
        assertTrue(file.exists() && file.length()> 100000);
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        cleanTestRDir();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
        cleanTestRDir();
    }
}
