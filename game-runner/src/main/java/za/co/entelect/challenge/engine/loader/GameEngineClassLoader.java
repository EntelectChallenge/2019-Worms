package za.co.entelect.challenge.engine.loader;

import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Set;

public class GameEngineClassLoader {

    private String gameEnginePath;

    public GameEngineClassLoader(String gameEnginePath) throws Exception {
        this.gameEnginePath = gameEnginePath;

        verifyGameEngineJar();
        loadEngineClasses();
    }

    private void verifyGameEngineJar() {
        // @TODO Check if the provided JAR is actually a game engine JAR
    }

    private void loadEngineClasses() throws Exception {

        File file = new File(gameEnginePath);

        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
    }

    public <T> T loadEngineObject(Class<T> cl) throws Exception {

        Reflections reflections = new Reflections("za.co.entelect");
        Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(cl);

        Iterator<Class<? extends T>> iterator = subTypesOf.iterator();
        if (!iterator.hasNext()) {
            throw new Exception(String.format("No implementation found for: %s", cl.getName()));
        }

        Class<? extends T> next = iterator.next();

        return next.newInstance();
    }
}
