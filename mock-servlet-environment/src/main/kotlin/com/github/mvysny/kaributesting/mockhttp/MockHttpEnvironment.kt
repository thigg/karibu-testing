package com.github.mvysny.kaributesting.mockhttp

import java.security.Principal
import java.util.*
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

open class MockServletConfig(val context: ServletContext) : ServletConfig {

    /**
     * Per-servlet init parameters.
     */
    var servletInitParams = mutableMapOf<String, String>()

    override fun getInitParameter(name: String): String? = servletInitParams[name]

    override fun getInitParameterNames(): Enumeration<String> = Collections.enumeration(servletInitParams.keys)

    override fun getServletName(): String = "Vaadin Servlet"

    override fun getServletContext(): ServletContext = context
}

internal fun <K, V> MutableMap<K, V>.putOrRemove(key: K, value: V?) {
    if (value == null) remove(key) else set(key, value)
}

object MockHttpEnvironment {
    /**
     * [HttpServletRequest.getLocalPort]
     */
    var localPort: Int = 8080

    /**
     * [HttpServletRequest.getServerPort]
     */
    var serverPort: Int = 8080

    /**
     * [HttpServletRequest.getUserPrincipal]
     */
    var userPrincipal: Principal? = null

    /**
     * [HttpServletRequest.getRemotePort]
     */
    var remotePort: Int = 8080
}
