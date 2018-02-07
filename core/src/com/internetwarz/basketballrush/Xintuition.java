package com.internetwarz.basketballrush;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.internetwarz.basketballrush.model.PlayerTurn;
import com.internetwarz.basketballrush.utils.Assets;
import com.internetwarz.basketballrush.utils.LanguagesManager;

import de.tomgrill.gdxfirebase.core.FirebaseConfiguration;
import de.tomgrill.gdxfirebase.core.FirebaseFeatures;
import de.tomgrill.gdxfirebase.core.FirebaseLoader;
import de.tomgrill.gdxfirebase.core.auth.FirebaseUser;
import de.tomgrill.gdxfirebase.core.auth.FirebaseUserBuilder;

public class Xintuition extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public FreeTypeFontGenerator generator;
    private static PlayServices playServices;
    private static TurnBasedService turnBasedService;
	public Assets assets;
	public FirebaseHelper firebaseHelper;
	public boolean isFirstStart = true;


    public Xintuition(PlayServices playServices, TurnBasedService service)
    {
        this.playServices = playServices;
        this.turnBasedService = service;

    }


	public static PlayServices getPlayServices() {
		return playServices;
	}

	public static TurnBasedService getTurnBasedService() {
		return turnBasedService;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Magistral Bold.TTF"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
			generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSansFallback.ttf"));
		parameter.size = 25;
		parameter.color= Color.valueOf("#506878");
		parameter.borderStraight = false;
		parameter.borderWidth = 1;
		parameter.borderColor = Color.valueOf("#e2e3e7");
		parameter.shadowColor = Color.valueOf("#141a1e");
		//parameter.shadowOffsetX = -1;
		//parameter.shadowOffsetY = -1;
		parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
		if(LanguagesManager.getInstance().getLanguage().equals("KO") || LanguagesManager.getInstance().getLanguage().equals("JA") || LanguagesManager.getInstance().getLanguage().equals("ZH"))
			parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "ただ手段ハード栄誉の殿堂統計遊びますゲームは終わった結果設定ただ手段ハードレベル実験右！間違いました！再起動はいいいえあなたは本当にゲームを再開しますか？ベストプレーヤーゲームは終わった！統計選手栄誉の殿堂ルールログアウト言語"
					+"명예의 전당통계놀이게임이결과설정다만방법단단한수평실험오른쪽잘못된다시 시작예아니오게임을 다시 시작 하시겠습니까?최고의 선수게임은끝났어!통계(플레이어)명예의 전당규칙로그 아웃언어"
					+"名人堂統計玩遊戲結束了結果設置只是手段硬水平實驗對錯了重新開始是的沒有你真的想重新啟動遊戲嗎？最好的球員遊戲結束了統計玩家名人堂規則註銷語言";
		font=generator.generateFont(parameter);

		//Loading all the assets in the assets class
		assets = new Assets();
		initDB();
        //Changing the screen to display splash screen
		this.setScreen(new SplashScreen(this));
//		this.setScreen(new DuelScreen(this,new PlayerTurn()));
//		this.setScreen(new XintuitionGameplayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		generator.dispose();
		font.dispose();
        assets.dispose();
	}

	private void initDB()
	{
		FirebaseUser firebaseUser = new FirebaseUserBuilder()
				.setUid("SUPER_SECRET_UID")
				.setName("Mike Pike")
				.setAnonymous(true)
				.build();


		FirebaseConfiguration config = new FirebaseConfiguration();
		config.desktopFirebaseUser = firebaseUser; // required on desktop

		config.databaseUrl = "https://tsar-10310718.firebaseio.com/"; // get this from Firebase console
		config.serviceAccount = Gdx.files.internal("Tsar-2ae2bff42014.json"); //

		FirebaseLoader.load(config,
				FirebaseFeatures.REALTIME_DATABASE // Just pass the enum for each FirebaseFeatures you want to enable.
				//  FirebaseFeatures.AUTHENTICATION
		);



		//firebaseHelper.getData("Hard");
		/*user = new User();
		user.setListEasy(firebaseHelper.getData("Easy"));
		user.setListMedium(firebaseHelper.getData("Medium"));
		user.setListHard(firebaseHelper.getData("Hard"));


		System.out.println(user.getListEasy());
		System.out.println(user.getListMedium());
		System.out.println(user.getListHard());

		HashMap<String, Integer> start = new HashMap<String, Integer>();
		start.put("level", 2);
		start.put("gamesCount", 1);
		user.getListEasy().add(start);

		firebaseHelper.updateList("Easy", user.getListEasy()); */

		//firebaseHelper = new FirebaseHelper("testguy@gmail.com");

/*
		firebaseHelper = new FirebaseHelper();
		firebaseHelper.dataInit();
*/
		//firebaseHelper.updateData("Hard", 8);


		/*ArrayList<UserScore> results = new ArrayList<UserScore>();
		results.add(new UserScore(1,2));
		results.add(new UserScore(2,3));
		results.add(new UserScore(3,4));*/
		//FirebaseHelper.saveUserScore(new UserScore(2, 10), "testguy@gmail.com", Constants.HARD_MODE, results);


	}
}

