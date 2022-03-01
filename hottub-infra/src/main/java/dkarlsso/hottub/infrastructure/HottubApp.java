package dkarlsso.hottub.infrastructure;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public final class HottubApp {


    public static void main(final String[] args) {
        final App app = new App();
        final HottubStack hottubStack = new HottubStack(app, "Hottub-stack", new StackProps.Builder().env(
                        Environment.builder()
                                .region("eu-north-1")
                                .account("145158422295")
                                .build())
                .build());
        app.synth();
    }
}
