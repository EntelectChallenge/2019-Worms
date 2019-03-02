package za.co.entelect.challenge.engine.loader;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import za.co.entelect.challenge.game.contracts.bootstrapper.EngineBootstrapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

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

        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Class<?> clazz = cl.getClass();

        Method method = clazz.getSuperclass().getDeclaredMethod("addURL", URL.class);

        method.setAccessible(true);
        method.invoke(cl, file.toURI().toURL());

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
