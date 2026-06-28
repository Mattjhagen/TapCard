package com.tapcard.app;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = TapCardApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface TapCardApplication_GeneratedInjector {
  void injectTapCardApplication(TapCardApplication tapCardApplication);
}
