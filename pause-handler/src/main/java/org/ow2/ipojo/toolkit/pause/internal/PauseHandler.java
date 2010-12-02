package org.ow2.ipojo.toolkit.pause.internal;

import java.util.Dictionary;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.FieldInterceptor;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.MethodMetadata;

@Handler(name = "pausecontroller",
         namespace = "org.ow2.ipojo.toolkit.pause")
public class PauseHandler extends PrimitiveHandler {

    private PausingInterceptor pausingInterceptor;

    @Override
    public void configure(Element metadata, Dictionary configuration)
            throws ConfigurationException {

        Element[] pauseElements = metadata.getElements("pausecontroller", "org.ow2.ipojo.toolkit.pause");
        String fieldName = pauseElements[0].getAttribute("field");
        FieldMetadata field = getPojoMetadata().getField(fieldName);

        pausingInterceptor = new PausingInterceptor();

        // Register the Controller
        getInstanceManager().register(field, new FieldInterceptor() {

            public void onSet(Object pojo, String fieldName, Object value) {
                if (Boolean.TRUE.equals(value)) {
                    // engage pause mechanism
                    pausingInterceptor.engage();
                } else {
                    // disengage pause mechanism
                    pausingInterceptor.disengage();
                }
            }

            public Object onGet(Object pojo, String fieldName, Object value) {
                return value;
            }
        });

        // Intercept all the POJO methods
        MethodMetadata[] methods = getPojoMetadata().getMethods();
        for (MethodMetadata method : methods) {
            getInstanceManager().register(method, pausingInterceptor);
        }
    }

    @Override
    public void stop() {
        // Force disengage when stopping (try to avoid locking forever)
        pausingInterceptor.disengage();
    }

    @Override
    public void start() { }

}
