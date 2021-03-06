package com.github.mvysny.kaributesting.v10

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.MenuItemBase
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import java.lang.reflect.Method
import kotlin.streams.toList

/**
 * If you need to hook into the testing lifecycle (e.g. you need to wait for any async operations to finish),
 * provide your own custom implementation of this interface, then set it into [testingLifecycleHook].
 *
 * ### Mocking server request end
 *
 * Since Karibu-Testing runs in the same JVM as the server and there is no browser, the boundaries between the client and
 * the server become unclear. When looking into sources of any test method, it's really hard to tell where exactly the server request ends, and
 * where another request starts.
 *
 * You can establish an explicit client boundary in your test, by explicitly calling [MockVaadin.clientRoundtrip]. However, since that
 * would be both laborous and error-prone, the default operation is that Karibu Testing pretends as if there was a client-server
 * roundtrip before every component lookup
 * via the [_get]/[_find]/[_expectNone]/[_expectOne] call. Therefore, [MockVaadin.clientRoundtrip] is called from [awaitBeforeLookup] by default.
 */
interface TestingLifecycleHook {
    /**
     * Invoked before every component lookup. You can e.g. wait for any async operations to finish and for the server to settle down.
     *
     * The default implementation calls the [MockVaadin.clientRoundtrip] method. When implementing this method, you should
     * also call [MockVaadin.clientRoundtrip] (or simply call super).
     */
    fun awaitBeforeLookup() {
        if (UI.getCurrent() != null) {
            MockVaadin.clientRoundtrip()
        }
    }

    /**
     * Invoked after every component lookup. You can e.g. wait for any async operations to finish and for the server to settle down.
     * Invoked even if the `_get()`/`_find()`/`_expectNone()` function fails.
     */
    fun awaitAfterLookup() {}

    /**
     * Provides all children of given component. Provides workarounds for certain components:
     * * For [Grid.Column] the function will also return cell components nested in all headers and footers for that particular column.
     * * For [MenuItemBase] the function returns all items of a sub-menu.
     */
    fun getAllChildren(component: Component): List<Component> = when(component) {
        is Grid.Column<*> -> {
            val grid: Grid<*> = component.grid
            val headerComponents: List<Component> = grid.headerRows.mapNotNull { it.getCell(component).component }
            val footerComponents: List<Component> = grid.footerRows.mapNotNull { it.getCell(component).component }
            headerComponents + footerComponents
        }
        is MenuItemBase<*, *, *> -> {
            component.getSubMenu().getItems()
        }
        else -> component.children.toList()
    }

    companion object {
        /**
         * A default lifecycle hook that simply runs default implementations of the hook functions.
         */
        val default: TestingLifecycleHook get() = object : TestingLifecycleHook {}
    }
}

/**
 * If you need to hook into the testing lifecycle (e.g. you need to wait for any async operations to finish),
 * set your custom implementation here. See [TestingLifecycleHook] for more info on
 * where exactly you can hook into.
 */
var testingLifecycleHook: TestingLifecycleHook = TestingLifecycleHook.default

/**
 * Flow Server does not close the dialog when [Dialog.close] is called; instead it tells client-side dialog to close,
 * which then fires event back to the server that the dialog was closed, and removes itself from the DOM.
 * Since there's no browser with browserless testing, we need to cleanup closed dialogs manually, hence this method.
 *
 * Also see [MockedUI] for more details
 */
fun cleanupDialogs() {
    UI.getCurrent().children.forEach {
        if (it is Dialog && !it.isOpened) {
            it.element.removeFromParent()
        }
    }

    // also clean up ConfirmDialog. But careful - this is a Pro component and may not be on classpath.
    val dlgClass: Class<*>? = try {
        Class.forName("com.vaadin.flow.component.confirmdialog.ConfirmDialog")
    } catch (e: ClassNotFoundException) { null }
    if (dlgClass != null) {
        val isOpenedMethod: Method = dlgClass.getMethod("isOpened")
        UI.getCurrent().children.forEach {
            if (dlgClass.isInstance(it) && !(isOpenedMethod.invoke(it) as Boolean)) {
                it.element.removeFromParent()
            }
        }
    }
}
