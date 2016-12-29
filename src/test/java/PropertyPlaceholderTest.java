import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PropertyPlaceholderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void propertiesAreOverridden() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("placeholder/context.xml");
        PropertyHolder holder = context.getBean(PropertyHolder.class);

        assertEquals("Property must be overridden by second.properties", "secondA", holder.getA());
        assertEquals("firstB", holder.getB());
        assertEquals("Property must be set to empty String", "", holder.getC());
    }

    @Test
    public void secondPlaceHolderIsIgnoredAsFirstFails() {
        thrown.expect(BeanDefinitionStoreException.class);
        thrown.expectMessage("Could not resolve placeholder 'b'");

        new ClassPathXmlApplicationContext("placeholder/context-placeholder-twice.xml");
    }

    @Test
    public void orderAttributeForOverridingPlaceholder() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("placeholder/context-placeholder-twice-order.xml");
        PropertyHolder holder = context.getBean(PropertyHolder.class);

        assertEquals("firstA", holder.getA());
        assertEquals("firstB", holder.getB());
        assertEquals("firstC", holder.getC());
    }

    @Test
    public void missingPropertyFileCausesException() {
        thrown.expect(BeanInitializationException.class);
        thrown.expectCause(IsInstanceOf.<FileNotFoundException>instanceOf(FileNotFoundException.class));
        thrown.expectMessage("missing.properties");

        new ClassPathXmlApplicationContext("placeholder/context-missing-property-file.xml");
    }

    @Test
    public void finalCheckForUnresolvable() {
        thrown.expect(BeanDefinitionStoreException.class);
        thrown.expectMessage("Could not resolve placeholder 'c'");

        new ClassPathXmlApplicationContext("placeholder/context-placeholder-unresolvable-check.xml");
    }


    public static class PropertyHolder {
        private String a;
        private String b;
        private String c;

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }

        public void setA(String a) {
            this.a = a;
        }

        public void setB(String b) {
            this.b = b;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getC() {
            return c;
        }

        @Override
        public String toString() {
            return "PropertyHolder{" +
                    "a='" + a + '\'' +
                    ", b='" + b + '\'' +
                    ", c='" + c + '\'' +
                    '}';
        }
    }
}
