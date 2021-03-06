package com.github.mvysny.kaributesting.v10

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.polymertemplate.Id
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.templatemodel.TemplateModel

/**
 * Tests that the Browserless testing approach is able to instantiate a polymer template properly, from
 * `src/main/webapp/frontend`.
 */
@PageTitle("Review List")
@Tag("reviews-list")
@JsModule("./src/reviews-list.js") // only present in kt10-testrun-vaadin14-npm
class ReviewsList : PolymerTemplate<TemplateModel>() {

    @Id("search")
    internal lateinit var search: TextField
    @Id("newReview")
    internal lateinit var addReview: Button
    @Id("header")
    internal lateinit var header: H1
}
