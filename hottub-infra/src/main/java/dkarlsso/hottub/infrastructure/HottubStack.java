package dkarlsso.hottub.infrastructure;


import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.LifecycleRule;
import software.constructs.Construct;

import java.util.Arrays;


public class HottubStack extends Stack {


    public HottubStack(Construct scope, String id, StackProps props) {
        super(scope, id, props);
        final String bucketName = "bathtub-statistics";
        Bucket.Builder.create(this, "SettingsBucket")
                        .bucketName(bucketName)
                        .publicReadAccess(false)
                        .removalPolicy(RemovalPolicy.RETAIN)
                        .versioned(true)
                        .lifecycleRules(Arrays.asList(LifecycleRule.builder()
                                        .enabled(true)
                                        .noncurrentVersionExpiration(Duration.days(180))
                                .build()))
                        .accessControl(BucketAccessControl.PRIVATE)
                        .build();
    }

}
