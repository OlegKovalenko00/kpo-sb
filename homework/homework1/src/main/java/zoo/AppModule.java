package zoo;

import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(VeterinaryClinic.class).to(VeterinaryClinicImpl.class);
    }
}
