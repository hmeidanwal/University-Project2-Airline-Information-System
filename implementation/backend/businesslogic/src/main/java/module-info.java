/**
 * Module businesslogic_module.
 */
module businesslogic_module {
    requires datarecords_module;
    requires persistence_api_module;
    requires businesslogic_api_module;
    requires org.slf4j;

    exports io.github.fontysvenlo.ais.businesslogic;
}
