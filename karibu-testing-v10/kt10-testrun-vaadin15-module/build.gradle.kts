dependencies {
    testImplementation("com.vaadin:vaadin:${properties["vaadin15_version"]}")
    testImplementation(project(":karibu-testing-v10:kt10-tests"))
}
