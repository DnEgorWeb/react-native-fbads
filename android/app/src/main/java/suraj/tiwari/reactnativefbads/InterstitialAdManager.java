package suraj.tiwari.reactnativefbads;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class InterstitialAdManager extends ReactContextBaseJavaModule implements InterstitialAdListener, LifecycleEventListener {

  private Promise mPromise;
  private Promise preloadedPromise;
  private boolean mDidClick = false;
  private InterstitialAd mInterstitial;
  private InterstitialAd preloadedInterstitial;

  public InterstitialAdManager(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addLifecycleEventListener(this);
  }

  @ReactMethod
  public void showAd(String placementId, Promise p) {
    if (mPromise != null) {
      p.reject("E_FAILED_TO_SHOW", "Only one `showAd` can be called at once");
      return;
    }
    ReactApplicationContext reactContext = this.getReactApplicationContext();

    mPromise = p;
    mInterstitial = new InterstitialAd(reactContext, placementId);
    mInterstitial.setAdListener(this);
    mInterstitial.loadAd();
  }

  @ReactMethod
  public void loadAd(String placementId, Promise p) {
    ReactApplicationContext reactContext = this.getReactApplicationContext();

    preloadedPromise = p;
    preloadedInterstitial = new InterstitialAd(reactContext, placementId);
    preloadedInterstitial.loadAd();
  }

  @ReactMethod
  public void showPreloadedAd() {
    if (preloadedInterstitial != null && preloadedInterstitial.isAdLoaded()) {
      preloadedInterstitial.show();
    }
  }

  @Override
  public String getName() {
    return "CTKInterstitialAdManager";
  }

  @Override
  public void onError(Ad ad, AdError adError) {
    mPromise.reject("E_FAILED_TO_LOAD", adError.getErrorMessage());
    cleanUp();
  }

  @Override
  public void onAdLoaded(Ad ad) {
    if (ad == mInterstitial) {
      mInterstitial.show();
    }
  }

  @Override
  public void onAdClicked(Ad ad) {
    mDidClick = true;
  }

  @Override
  public void onInterstitialDismissed(Ad ad) {
    mPromise.resolve(mDidClick);
    cleanUp();
  }

  @Override
  public void onInterstitialDisplayed(Ad ad) {

  }

  @Override
  public void onLoggingImpression(Ad ad) {
  }

  private void cleanUp() {
    mPromise = null;
    mDidClick = false;

    if (mInterstitial != null) {
      mInterstitial.destroy();
      mInterstitial = null;
    }
  }

  @Override
  public void onHostResume() {

  }

  @Override
  public void onHostPause() {

  }

  @Override
  public void onHostDestroy() {
    cleanUp();
  }
}
