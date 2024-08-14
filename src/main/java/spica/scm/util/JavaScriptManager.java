/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author java-2
 */
public class JavaScriptManager {

  /**
   * Returns Java script engine.
   *
   * @param scriptFile
   * @return
   * @throws ScriptException
   * @throws IOException
   * @throws NoSuchMethodException
   */
  public static ScriptEngine getScriptEngine(String scriptFile) throws ScriptException, IOException, NoSuchMethodException {
    ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
    // reads java script file
    scriptEngine.eval(Files.newBufferedReader(Paths.get(scriptFile), StandardCharsets.UTF_8));

    return scriptEngine;
  }

  /**
   * Method calls java script function and returns the returned value from java script function
   *
   * @param scriptEngine
   * @param functionName
   * @param params
   * @return
   * @throws ScriptException
   * @throws NoSuchMethodException
   */
  public static Object invokeFunction(ScriptEngine scriptEngine, String functionName, Object... params) throws ScriptException, NoSuchMethodException {
    Object rvalue = null;
    if (scriptEngine != null) {
      Invocable invocable = (Invocable) scriptEngine;
      rvalue = invocable.invokeFunction(functionName, params);
    }
    return rvalue;
  }
}
