package com.internetwarz.basketballrush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.internetwarz.basketballrush.AndroidLauncher;
import com.internetwarz.basketballrush.R;

import java.io.IOException;
import java.security.Security;


public class AndroidLauncher2 extends AndroidApplication implements IActivityRequestHandler, INativeRequestHandler, ActionResolver, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, RewardedVideoAdListener
{
    private final int SHOW_ADS=1;
    private final int HIDE_ADS=0;
    private InterstitialAd interstitial;
    private AdView nadView;
    private boolean nativeloaded,interloaded;
    private RewardedVideoAd mAd;
    private boolean showingnative=false;
    private AdRequest adRequest;
    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressWarnings("unused")
    private static final int PC=0;
    private static final int genericAndroid=1;
    @SuppressWarnings("unused")
    private static final int fireTV=2;
    @SuppressWarnings("unused")
    private static final int OUYAdevice=3;

    private boolean billingavailable;

    private static final String TAG="com.clockwatchers.monstersolitaire";

    private static final String admobappid="xxx";

    private static final String rewardid="xxx";
    private static final String interstitialid="xxx";
    private static final String largenativeid="xxx";

    private static final String base64EncodedPublicKey="XXX";


    private static final String leaderboardstring="xxx";
    IabHelper billingHelper;

    private GameLang lang;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean dowehavefocus;
    private boolean rewardready=false;
    private static final int MAX_SNAPSHOT_RESOLVE_RETRIES = 3;
    private GoogleApiClient mGoogleApiClient;
    private Snapshots.OpenSnapshotResult open;

    private byte[] mSaveGameData;
    private Snapshot snapshot,mResolvedSnapshot;
    private static int RC_SIGN_IN=9001;
    private int retryCount=0;
    private boolean mResolvingConnectionFailure=false;
    private boolean mAutoStartSignInflow=true;
    private boolean mSignOut=false;
    private boolean autosignin=true;
    private boolean itemsent=false;
    private BuyApp iap;
    private float soundvolume;

    private boolean taguser=false;
    private long dayssinceinstall=0;

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener=new IabHelper.OnIabPurchaseFinishedListener()
    {
        public void onIabPurchaseFinished(IabResult result,Purchase purchase)
        {
            Log.d(TAG,"Billing : Purchase finished - "+result+", purchase: "+purchase);
            if(result.isFailure())
            {
                Log.d(TAG,"Billing : onIabPurchaseFinished failed: "+result);
                return;
            }
            else
            {
                for(int w=0;w<SharedVariables.wiap;w++)
                    for(int o=0;o<SharedVariables.oiap;o++)
                    {
                        if(purchase.getSku().equals(lang.iap[w][o].sku))
                        {
                            Log.d(TAG,"Billing : onIabPurchaseFinished Success "+purchase.getSku());
                            billingHelper.consumeAsync(purchase,mConsumeFinishedListener);
                        }
                    }
            }

        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener=new IabHelper.OnConsumeFinishedListener()
    {
        public void onConsumeFinished(Purchase purchase,IabResult result)
        {

            if(result.isSuccess())
            {
                Log.d(TAG,"Billing Consume Success : "+purchase.getSku());
                for(int w=0;w<SharedVariables.wiap;w++)
                    for(int o=0;o<SharedVariables.oiap;o++)
                    {
                        if(purchase.getSku().equals(lang.iap[w][o].sku)
                                && purchase.getDeveloperPayload().equals(admobappid+purchase.getSku())==true
                                && Security.verifyPurchase(base64EncodedPublicKey,purchase.getOriginalJson() ,purchase.getSignature())==true
                                )
                        {
                            lang.iap[w][o].purchased=true;
                            Log.d(TAG,"Billing Consume Set flag for : "+lang.iap[w][o].sku);
                        }
                    }
            }
            else
            {
                // handle error
                Log.d(TAG,"Billing Consume Error");
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener=new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result,Inventory inventory)
        {
            Log.d(TAG,"Billing Query inventory finished.");
            if(result.isFailure())
            {
                // Handle failure
                Log.d(TAG,"Billing Query inventory failure "+result);
            }
            else
            {
                for(int w=0;w<SharedVariables.wiap;w++)
                    for(int o=0;o<SharedVariables.oiap;o++)
                    {
                        if(inventory.getPurchase(lang.iap[w][o].sku)!=null)
                        {
                            Log.d(TAG,"Billing Query inventory success.");
                            billingHelper.consumeAsync(inventory.getPurchase(lang.iap[w][o].sku),mConsumeFinishedListener);
                        }
                        else
                            Log.d(TAG,"Billing checkPurchase==null");
                    }
            }
        }
    };

    protected Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case SHOW_ADS:
                {
                    if(interstitial.isLoaded())
                        interstitial.show();
                    else
                        lang.showingad=false;
                    break;
                }
                case HIDE_ADS:
                {
                    lang.showingad=false;
                    break;
                }
            }
        }
    };

    protected Handler rewardedhandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case SHOW_ADS:
                {
                    if (mAd.isLoaded())
                    {
                        if(soundvolume<0.2f)
                            soundvolume=0.2f;
                        if(soundvolume>1.0f)
                            soundvolume=1.0f;
                        MobileAds.setAppVolume(soundvolume);

                        Log.d(TAG,"Show Rewarded : "+soundvolume);
                        mAd.show();
                    }
                    break;
                }
                case HIDE_ADS:
                {
                    break;
                }
            }
        }
    };


    protected Handler lnhandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            Log.d(TAG,"Native Show L : "+settings.getBoolean("paidnoads",false)+" "+nativeloaded);

            switch(msg.what)
            {
                case SHOW_ADS:
                {
                    if(nativeloaded==false)
                    {
                        nadView.loadAd(new AdRequest.Builder()
                                .addTestDevice("2E6E1B356395A0179393B85EB9F65D0F")// nexus 7

                                .addTestDevice("8D59043B27C1CDA6683F7E0F79228D03")// nexus
                                // 9
                                .addTestDevice("FD5ADEEDE051BFF6F79B61140C964E60")// phone
                                .build());
                    }
                    if(nativeloaded==true)
                    {
                        showingnative=true;
                        nadView.setBackgroundColor(Color.TRANSPARENT);
                        nadView.setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case HIDE_ADS:
                {
                    nadView.setVisibility(View.GONE);

                    if(showingnative==true)
                    {
                        showingnative=false;
                        nativeloaded=false;
                        nadView.loadAd(new AdRequest.Builder()
                                .addTestDevice("2E6E1B356395A0179393B85EB9F65D0F")//nexus 7
                                .addTestDevice("8D59043B27C1CDA6683F7E0F79228D03")// nexus 9
                                .build());
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onPause()
    {
        mAd.pause();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        mAd.resume();
        super.onResume();
        if(lang.focus==false)
            lang.focus=hasWindowFocus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        // Create the layout
        RelativeLayout layout=new RelativeLayout(this);

        MobileAds.initialize(this, admobappid);


        // Do the stuff that initialize() would do for you
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // analytics
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);
        // end analytics


        lang=new GameLang();
        settings=getSharedPreferences("monstersolitaireads",0);
        editor=settings.edit();
        loadprefs();
        iap=new BuyApp();



        billingavailable=true;

        if(isGooglePlayAvailable()==false)
            billingavailable=false;
        else
        {
            billingHelper=new IabHelper(this,base64EncodedPublicKey);

            billingHelper.enableDebugLogging(true);

            billingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
            {
                public void onIabSetupFinished(IabResult result)
                {
                    if(!result.isSuccess())
                    {
                        billingavailable=false;
                        Log.d(TAG,"In-app Billing setup failed: "+result);
                    }
                    else
                    {
                        Log.d(TAG,"In-app Billing is set up OK");
                        billingHelper.queryInventoryAsync(mReceivedInventoryListener);
                    }
                }
            });

        } // end check if google billing is available

        //google play services

        if(isGooglePlayAvailable()==true)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)

                    // add other APIs and scopes here as needed
                    .build();
        }


        // start languages

        lang.game=this.getResources().getString(R.string.game);

        lang.viewvideo=this.getResources().getString(R.string.viewvideo);
        lang.reward=this.getResources().getString(R.string.reward);
        lang.leaderboardpoints=this.getResources().getString(R.string.leaderboardpoints);

        lang.swimmingfish=this.getResources().getString(R.string.swimmingfish);
        lang.policy=this.getResources().getString(R.string.policy);
        lang.moregames=this.getResources().getString(R.string.moregames);
        lang.playmoregames=this.getResources().getString(R.string.playmoregames);
        lang.threestarsneeded=this.getResources().getString(R.string.threestarsneeded);
        lang.alreadyopened=this.getResources().getString(R.string.alreadyopened);
        lang.solvepuzzle=this.getResources().getString(R.string.solvepuzzle);
        lang.tounlock=this.getResources().getString(R.string.tounlock);
        lang.select5=this.getResources().getString(R.string.select5);
        lang.select5cards=this.getResources().getString(R.string.select5cards);
        lang.selecttreasure=this.getResources().getString(R.string.selecttreasure);

        lang.wildspecific=this.getResources().getString(R.string.wildspecific);
        lang.wildmultiple=this.getResources().getString(R.string.wildmultiple);
        lang.wildwildcard=this.getResources().getString(R.string.wildwildcard);
        lang.wildupdown=this.getResources().getString(R.string.wildupdown);
        lang.wildrightleft=this.getResources().getString(R.string.wildrightleft);

        lang.collectstarfish=this.getResources().getString(R.string.collectstarfish);
        lang.towinatreasure=this.getResources().getString(R.string.towinatreasure);
        lang.newwildcardunlocked=this.getResources().getString(R.string.newwildcardunlocked);
        lang.rate=this.getResources().getString(R.string.rate);
        lang.rewarded=this.getResources().getString(R.string.rewarded);
        lang.failed=this.getResources().getString(R.string.failed);


        lang.connectgp=this.getResources().getString(R.string.connectgp);
        lang.disconnectgp=this.getResources().getString(R.string.disconnectgp);
        lang.allpuzzles=this.getResources().getString(R.string.allpuzzles);

        lang.tutorial[0]=this.getResources().getString(R.string.tutorial0);
        lang.tutorial[1]=this.getResources().getString(R.string.tutorial1);
        lang.tutorial[2]=this.getResources().getString(R.string.tutorial2);
        lang.tutorial[3]=this.getResources().getString(R.string.tutorial3);
        lang.tutorial[4]=this.getResources().getString(R.string.tutorial4);
        lang.tutorial[5]=this.getResources().getString(R.string.tutorial5);
        lang.tutorial[6]=this.getResources().getString(R.string.tutorial6);
        lang.tutorial[7]=this.getResources().getString(R.string.tutorial7);
        lang.tutorial[8]=this.getResources().getString(R.string.tutorial8);
        lang.tutorial[9]=this.getResources().getString(R.string.tutorial9);
        lang.tutorial[10]=this.getResources().getString(R.string.tutorial10);
        lang.tutorial[11]=this.getResources().getString(R.string.tutorial11);
        lang.tutorial[12]=this.getResources().getString(R.string.tutorial12);
        lang.tutorial[13]=this.getResources().getString(R.string.tutorial13);
        lang.tutorial[14]=this.getResources().getString(R.string.tutorial14);
        lang.tutorial[15]=this.getResources().getString(R.string.tutorial15);

        lang.nowildundo=this.getResources().getString(R.string.nowildundo);
        lang.noundo=this.getResources().getString(R.string.noundo);

        lang.collect3=this.getResources().getString(R.string.collect3);


        lang.lang=this.getResources().getString(R.string.lang);

        lang.showingad=false;
        lang.standard=Integer.parseInt(this.getResources().getString(R.string.standard));


        AndroidApplicationConfiguration cfg=new AndroidApplicationConfiguration();
        cfg.useAccelerometer=false;
        cfg.useCompass=false;
        cfg.hideStatusBar=true;
        cfg.useImmersiveMode=true;

        View gameView=initializeForView(new MonsterSolitaire(this,this,this,lang,genericAndroid),cfg);

        // Create the interstitial
        interstitial=new InterstitialAd(this);
        interstitial.setAdUnitId(interstitialid);
        interloaded=false;
        adRequest=new AdRequest.Builder()
                //.addTestDevice("2E6E1B356395A0179393B85EB9F65D0F")// nexus 7
                .addTestDevice("8D59043B27C1CDA6683F7E0F79228D03")// nexus 9
                .addTestDevice("FD5ADEEDE051BFF6F79B61140C964E60")// phone
                .build(); // nexus 7

        // Begin loading your regular interstitial
        if(settings.getBoolean("paidnoads",false)==false)
            interstitial.loadAd(adRequest);
        // Set Ad Listener to use the callbacks below
        interstitial.setAdListener(new AdListener()
        {
            public void onAdLoaded()
            {
                Log.d(TAG,"Interstitial loaded");
                interloaded=true;
            }

            public void onAdFailedToLoad(int errorcode)
            {
                lang.showingad=false;
                interloaded=false;
            }

            public void onAdClosed()
            {
                lang.showingad=false;
                interloaded=false;
                interstitial.loadAd(adRequest);
            }

            public void onAdLeftApplication()
            {
                sendItemToFireBase(true);
                Log.d(TAG,"Interstitial Left App");
            }
        });

        // Add the libgdx view
        layout.addView(gameView);

        // Add the AdMob view
        RelativeLayout.LayoutParams adParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int lx=450;
        int ly=375;
        int check=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,lx,getResources().getDisplayMetrics());
        if(check>(int) (metrics.heightPixels*0.90f))
        {
            lx=300;
            ly=250;
        }
        // Log.d(TAG, "check "+lx+" "+ly);

        nadView=new AdView(this);
        nadView.setAdUnitId(largenativeid);
        AdSize lnativeAdSize=new AdSize(lx,ly);
        nadView.setAdSize(lnativeAdSize);
        int rw=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,lx,getResources().getDisplayMetrics());
        int rh=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,ly,getResources().getDisplayMetrics());
        lang.rectwl=rw;
        lang.recthl=rh;

        RelativeLayout.LayoutParams nativeParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        nativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        nadView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded()
            {
                nativeloaded=true;
                Log.d(TAG,"L Native Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorcode)
            {
                Log.d(TAG,"L Native Error "+errorcode);
            }

            public void onAdLeftApplication()
            {
                sendItemToFireBase(true);
                Log.d(TAG,"Native Left App");
            }

        });

        nadView.loadAd(new AdRequest.Builder()
                .addTestDevice("2E6E1B356395A0179393B85EB9F65D0F")//nexus 7
                .addTestDevice("0A9129B5A4DBA984B6A77648439AE967")// nexus 9
                .build());
        Log.d(TAG,"L "+lang.recthl);

        nativeloaded=false;

        // finish creatind adview

        layout.addView(nadView,nativeParams);

        setContentView(layout);


        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        mAd.loadAd(rewardid, new AdRequest.Builder().build());

        nadView.setBackgroundColor(Color.TRANSPARENT);
        nadView.setVisibility(View.GONE);
    }

    // begin billing
    @Override
    public void buyItem(int w, int o)
    {
        Log.d(TAG,"Billing : Purchase started - "+lang.iap[w][o].sku);
        billingHelper.launchPurchaseFlow(this,lang.iap[w][o].sku,10001,mPurchaseFinishedListener,admobappid+lang.iap[w][o].sku);
        //lang.iap[w][o].purchased=true;
    }

    @Override
    public boolean hasPurchased(int w, int o)
    {
        if(lang.iap[w][o].purchased==true)
        {
            lang.iap[w][o].purchased=false;
            editor.putBoolean("paidnoads",true);
            editor.apply();
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean isBillingAvailable()
    {
        return billingavailable;
    }

// end billing

    @Override
    public void onDestroy()
    {
        mAd.destroy();
        super.onDestroy();
        if(billingHelper!=null)
            billingHelper.dispose();

        billingHelper=null;
        Log.d(TAG,"destroy");

    }

// end billing

    @Override
    public void showAds(boolean show)
    {
        handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(mGoogleApiClient!=null&&autosignin==true)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(TAG,"stop");
        if(lang!=null)
            lang.focus=false;
        if(mGoogleApiClient!=null)
            mGoogleApiClient.disconnect();
    }

    @Override
    protected void onActivityResult(int request,int response,Intent data)
    {
        if(!billingHelper.handleActivityResult(request,response,data))
        {
            super.onActivityResult(request,response,data);
        }
    }

    public boolean isSignedIn()
    {
        if(mGoogleApiClient!=null)
            return mGoogleApiClient.isConnected();
        else
            return false;
    }

    @Override
    public boolean getSignedInGPGS()
    {
        return isSignedIn();
    }

    @Override
    public void loginGPGS()
    {
        if(isGooglePlayAvailable()==true)
        {
            if(mGoogleApiClient!=null)
            {
                mAutoStartSignInflow=true;
                mGoogleApiClient.connect();
                editor.putBoolean("autosignin",true);
                editor.apply();
            }
        }
    }

    @Override
    public void logoutGPGS()
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true)
        {
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mSignOut=true;
            editor.putBoolean("autosignin",false);
            editor.apply();
        }

    }

    @Override
    public void submitScoreGPGS(long score)
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true)
        {
            Games.Leaderboards.submitScore(mGoogleApiClient,leaderboardstring,score);

            Bundle bundle=new Bundle();
            bundle.putLong(FirebaseAnalytics.Param.SCORE,score);
            if(mFirebaseAnalytics!=null)
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,bundle);
        }
    }

    @Override
    public void unlockAchievementGPGS(String achievementId)
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true)
        {
            Games.Achievements.unlock(mGoogleApiClient,achievementId);

            Bundle bundle=new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, achievementId);
            if(mFirebaseAnalytics!=null)
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,bundle);
        }
    }

    @Override
    public void incrementAchievementGPGS(String achievementId,int amount)
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true)
            Games.Achievements.increment(mGoogleApiClient,achievementId,amount);
    }

    @Override
    public void getLeaderboardGPGS()
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true)
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,leaderboardstring),123456);
    }

    @Override
    public void getAchievementsGPGS()
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true)
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),12345);
    }

    @Override
    public boolean isPlusAvailable()
    {
        return isGooglePlayAvailable();
    }

    @Override
    public void saveGame(String gamevars)
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true)
            writeSnapshot(gamevars.getBytes(), null);
    }

    @Override
    public void loadCloudData()
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true && lang.gotclouddata==false && lang.loadingcloud==false)
        {
            Log.d(TAG,"loadCloudData");
            loadFromSnapshot();
        }
    }


    @Override
    public String loadGamedata()
    {
        return new String(mSaveGameData);
    }

    public boolean isGooglePlayAvailable()
    {
        boolean googlePlayStoreInstalled;
        int val=GooglePlayServicesUtil.isGooglePlayServicesAvailable(AndroidLauncher.this);
        googlePlayStoreInstalled=(val==ConnectionResult.SUCCESS);
        return googlePlayStoreInstalled;
    }

    @Override
    public boolean haveFocus()
    {
        return dowehavefocus;
    }

    @Override
    public void setScreenName(String screenname)
    {
        Bundle bundle=new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,screenname);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"screen");
        if(mFirebaseAnalytics!=null)
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,bundle);
        Log.d(TAG,"screenname : "+screenname);


        if(screenname.contains("Game ")==true)
        {
            String level=new String();

            int o=0;
            while(screenname.charAt(o)!=' ')
                o++;
            o++;
            while(o<screenname.length())
                level=level+screenname.charAt(o++);

            if(validNumber(level,false)==true)
            {
                Bundle bundle2=new Bundle();
                bundle2.putLong(Param.LEVEL, Long.valueOf(level));
                if(mFirebaseAnalytics!=null)
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,bundle2);
            }

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        dowehavefocus=hasFocus;
        Log.d(TAG,"focus : "+hasFocus);
        if(lang!=null)
            lang.focus=hasFocus;
    }

    public void loadprefs()
    {
        taguser=settings.getBoolean("taguser",false);
        itemsent=settings.getBoolean("itemsent",false);

        lang.maxinterstitials=settings.getInt("maxinterstitials",8);
        lang.interstitialdelaysecs=settings.getInt("interstitialdelaysecs",35);
        lang.rewardsolves=settings.getInt("rewardsolves",1);
        lang.nativedelaysecs=settings.getInt("nativedelaysecs",35);
        lang.replacenative=settings.getInt("replacenative",5);

        lang.disableinterstitials=settings.getBoolean("disableinterstitials",false);
        lang.disablenatives=settings.getBoolean("disablenatives",false);



        for(int w=0;w<SharedVariables.wiap;w++)
            for(int o=0;o<SharedVariables.oiap;o++)
                lang.iap[w][o]=new BuyApp();

        if(settings.contains("iap-0-0-sku")==true)
        {
            String mkey;
            for(int w=0;w<SharedVariables.wiap;w++)
                for(int o=0;o<SharedVariables.oiap;o++)
                {
                    mkey="iap-"+w+"-"+o+"-";
                    lang.iap[w][o].sku=settings.getString(mkey+"sku","");
                    lang.iap[w][o].price=settings.getFloat(mkey+"price",0);
                    lang.iap[w][o].quantity=settings.getInt(mkey+"quantity",0);
                    lang.iap[w][o].percentage=settings.getInt(mkey+"percentage",0);

                    //Log.d(TAG,"prefs "+mkey+" "+lang.iap[w][o].sku+" "+lang.iap[w][o].price+" "+lang.iap[w][o].quantity+" "+lang.iap[w][o].percentage);
                }
        }
        else
        {
            for(int w=0;w<SharedVariables.wiap;w++)
                for(int o=0;o<SharedVariables.oiap;o++)
                {
                    lang.iap[w][o].sku="monster_"+w+"_"+o;
                    switch (o)
                    {
                        case 0:
                        {
                            lang.iap[w][o].price=0.99f;
                            lang.iap[w][o].quantity=2;
                            lang.iap[w][o].percentage=0;
                            break;
                        }
                        case 1:
                        {
                            lang.iap[w][o].price=1.99f;
                            lang.iap[w][o].quantity=5;
                            lang.iap[w][o].percentage=20;
                            break;
                        }
                        case 2:
                        {
                            lang.iap[w][o].price=4.99f;
                            lang.iap[w][o].quantity=20;
                            lang.iap[w][o].percentage=40;
                            break;
                        }
                    }
				/*
				if(w==1)
					lang.iap[w][o].quantity=lang.iap[w][o].quantity*2;
				else
					{
					if(w==6 || w==5 || w==3)
						lang.iap[w][o].quantity=lang.iap[w][o].quantity*3/2;
					}
					*/
                }
        }



        if(Build.VERSION.SDK_INT>=11)
        {
            PackageManager myapp=this.getPackageManager();
            String installer=myapp.getInstallerPackageName(getApplicationContext().getPackageName());
            try
            {
                long installdate=myapp.getPackageInfo(getApplicationContext().getPackageName(), 0).firstInstallTime;
                long today=System.currentTimeMillis();

                installdate=installdate/(1000*60*60*24);
                today=today/(1000*60*60*24);
                dayssinceinstall=today-installdate;

                Log.d(TAG,"days : "+dayssinceinstall+" "+itemsent);
                if(dayssinceinstall==2 && itemsent==false)
                {
                    editor.putBoolean("itemsent",true);
                    editor.apply();

                    Bundle bundle=new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"dayssinceinstall");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID,"three");
                    if(mFirebaseAnalytics!=null)
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM,bundle);
                    Log.d(TAG,"Sending Item");
                }
            }
            catch (NameNotFoundException e)
            {
                //should never happen
                return;
            }

            Log.d(TAG,"installer : "+installer);
            if(installer!=null)
            {
                if(installer.equals("com.android.vending")||installer.contains("com.google.android"))
                    lang.url=settings.getString("url","https://play.google.com/store/apps/dev?id=5421036283029867644");
            }
        }

    }

    public void sendItemToFireBase(boolean tagged)
    {
        Bundle bundle=new Bundle();
        if(mFirebaseAnalytics!=null)
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.GENERATE_LEAD,bundle);
    }

    public int bannerHeight(boolean large)
    {
        int bh;
        bh=0;

        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeightInDP=(int) ((float) metrics.heightPixels/(float) metrics.density);

        bh=screenHeightInDP;

        if(large==true)
            bh=250;
        else
            bh=160;
        bh=(int) (bh*metrics.density);

        return bh;
    }

    @Override
    public boolean rewardReady()
    {
        return rewardready;
    }

    @Override
    public void showVideo(float volume)
    {
        Log.d(TAG, "rewarded : "+rewardready);
        if(rewardready==true)
        {
            soundvolume=volume;
            lang.rewardearned=false;
            rewardready=false;
            rewardedhandler.sendEmptyMessage(SHOW_ADS);
        }
    }

    @Override
    public void showNative(boolean show)
    {
        Log.d(TAG,"showNative : "+show);
        lnhandler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
    }

    @Override
    public boolean nativeReady()
    {
        return nativeloaded;
    }

    @Override
    public boolean interStitialReady()
    {
        return interloaded;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if(mResolvingConnectionFailure)
        {
            // Already resolving
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if(mAutoStartSignInflow==true)
        {
            mAutoStartSignInflow=false;
            mResolvingConnectionFailure=true;

            if(!BaseGameUtils.resolveConnectionFailure(this,mGoogleApiClient,connectionResult,RC_SIGN_IN,"Error Signing In To Google Play"))
            {
                mResolvingConnectionFailure=false;
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0)
    {
        mSignOut=false;
        editor.putBoolean("autosignin",true);
        editor.apply();

        if(isGooglePlayAvailable()==true&&isSignedIn()==true && lang.gotclouddata==false && lang.loadingcloud==false)
            loadFromSnapshot();
    }

    @Override
    public void onConnectionSuspended(int arg0)
    {
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();
    }

    private PendingResult<Snapshots.CommitSnapshotResult> writeSnapshot(byte[] data, Bitmap coverImage)
    {
        open = Games.Snapshots.open(mGoogleApiClient, TAG,true).await();
        if(open.getStatus().isSuccess()==true)
        {
            //Log.d(TAG,"saveGame : "+new String(data));

            snapshot = open.getSnapshot();

            // Set the data payload for the snapshot
            snapshot.getSnapshotContents().writeBytes(data);

            // Create the change operation
            SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                    //.setCoverImage(coverImage)
                    .setDescription(TAG)
                    .build();

            // Commit the operation
            return Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, metadataChange);
        }
        else
            return null;
    }

    private void loadFromSnapshot()
    {
        if(isGooglePlayAvailable()==true&&isSignedIn()==true&&lang.gotclouddata==false&&lang.loadingcloud==false)
        {
            lang.cloudloaded=false;
            lang.loadingcloud=true;
            // Display a progress dialog
            // ...

            AsyncTask<Void, Void, Integer> task=new AsyncTask<Void, Void, Integer>()
            {
                @Override
                protected Integer doInBackground(Void... params)
                {
                    // Open the saved game using its name.
                    Snapshots.OpenSnapshotResult result=Games.Snapshots.open(mGoogleApiClient,TAG,true).await();
                    retryCount++;
                    // Check the result of the open operation
                    if(result.getStatus().isSuccess())
                    {
                        Snapshot snapshot=result.getSnapshot();
                        // Read the byte content of the saved game.
                        try
                        {
                            mSaveGameData=snapshot.getSnapshotContents().readFully();
                            //Log.e(TAG,"Snapshot read : "+new String(mSaveGameData));
                            lang.cloudloaded=true;
                            lang.loadingcloud=false;
                        }
                        catch (IOException e)
                        {
                            lang.loadingcloud=false;
                            Log.e(TAG,"Error while reading Snapshot.",e);
                        }
                    }
                    else
                    {
                        Log.e(TAG,"Error while loading: "+result.getStatus().getStatusCode());
                        if(result.getStatus().getStatusCode()==GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT)
                        {
                            Snapshot snapshot=result.getSnapshot();
                            Snapshot conflictSnapshot=result.getConflictingSnapshot();

                            // Resolve between conflicts by selecting the newest of the
                            // conflicting snapshots.
                            mResolvedSnapshot=snapshot;

                            if(snapshot.getMetadata().getLastModifiedTimestamp()<conflictSnapshot.getMetadata().getLastModifiedTimestamp())
                                mResolvedSnapshot=conflictSnapshot;

                            Snapshots.OpenSnapshotResult resolveResult=Games.Snapshots.resolveConflict(mGoogleApiClient,result.getConflictId(),mResolvedSnapshot).await();

                            if(retryCount<MAX_SNAPSHOT_RESOLVE_RETRIES)
                            {
                                // Recursively attempt again
                                lang.loadingcloud=false;
                                loadFromSnapshot();
                            }

                        }
                        lang.loadingcloud=false;
                    }

                    return result.getStatus().getStatusCode();
                }

                @Override
                protected void onPostExecute(Integer status)
                {
                    // Dismiss progress dialog and reflect the changes in the UI.
                    // ...
                    lang.loadingcloud=false;
                }
            };

            task.execute();
        }
    }

    @Override
    public void onRewarded(RewardItem arg0)
    {
        lang.stopreward=true;
        lang.rewardearned=true;
        Log.i(TAG,"onRewarded");
    }

    @Override
    public void onRewardedVideoAdClosed()
    {
        lang.stopreward=true;
        if(mAd.isLoaded()==false)
            mAd.loadAd(rewardid, new AdRequest.Builder().build());
        Log.i(TAG,"onRewardedVideoAdClosed");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int arg0)
    {
        Log.i(TAG,"onRewardedVideoAdFailed");
    }

    @Override
    public void onRewardedVideoAdLeftApplication()
    {
        sendItemToFireBase(true);
        Log.i(TAG,"onRewardedVideoAdLeftApp");
    }

    @Override
    public void onRewardedVideoAdLoaded()
    {
        rewardready=true;
        Log.i(TAG,"onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened()
    {
        Log.i(TAG,"onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted()
    {
        Log.i(TAG,"onRewardedVideoAdStarted");
    }

    public boolean validNumber( String val,boolean havedecimal)
    {
        boolean goodnumber=true;
        int i=0;

        while(i<val.length() && goodnumber==true)
        {
            if(val.charAt(i)<'0'|| val.charAt(i)>'9')
            {
                //System.out.println("at "+val.charAt(i));

                if(havedecimal==true && val.charAt(i)=='.')
                    i++;
                else
                    goodnumber=false;
            }
            else
                i++;
        }
        return goodnumber;
    }


}
