package com.mygdx.bird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

    final Bird game;
    Texture backgroundImage;
    Texture birdImage;
    Texture pipeUpImage;
    Texture pipeDownImage;
    Sound flapSound;
    Sound failSound;
    Sound explosion1;

    OrthographicCamera camera;

    Array<Rectangle> obstacles;
    Array<Rectangle> brokenobstacles;
    Array<Rectangle> powerups;

    long lastObstacleTime;
    long lastObstacleTime2;
    long lastObstacleTime3;

    Rectangle player;


    Texture powerupImage;
    Texture birdblueImage;
    Texture birdredImage;
    boolean vulnerabilidad;
    boolean changecolor;


    float speedy;
    float gravity;
    boolean dead;
    boolean finaldead;
    boolean oneTime;
    float score;

    float pipeYposition;
    float pipeXposition;


    Texture part1Image;
    Texture part2Image;
    Texture part3Image;
    Texture part4Image;

    Rectangle part1;
    Rectangle part2;
    Rectangle part3;
    Rectangle part4;

    Texture part1PipeUpImage;
    Texture part2PipeUpImage;

    Rectangle part1pipe;
    Rectangle part2Pipe;


    public GameScreen(final Bird gam) {
        this.game = gam;
        backgroundImage = new Texture(Gdx.files.internal("background.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        birdImage = new Texture(Gdx.files.internal("bird.png"));

        flapSound = Gdx.audio.newSound(Gdx.files.internal("flap.wav"));
        failSound = Gdx.audio.newSound(Gdx.files.internal("fail.wav"));
        explosion1 = Gdx.audio.newSound(Gdx.files.internal("explosion1.wav"));

        // create a Rectangle to logically represent the player
        player = new Rectangle();

        part1 = new Rectangle();
        part2 = new Rectangle();
        part3 = new Rectangle();
        part4 = new Rectangle();

        part1pipe = new Rectangle();
        part2Pipe = new Rectangle();


        player.x = 200;
        player.y = 480 / 2 - 45 / 2;

        part1.x = player.x; part1.y = player.y;
        part2.x = player.x; part2.y = player.y;
        part3.x = player.x; part3.y = player.y;
        part4.x = player.x; part4.y = player.y;


        player.width = 64;
        player.height = 45;

        speedy = 0;
        gravity = 850f;
        dead = false;
        finaldead = false;
        oneTime = true;
        score = 0;


        pipeUpImage = new Texture(Gdx.files.internal("pipe_up.png"));
        pipeDownImage = new Texture(Gdx.files.internal("pipe_down.png"));
        powerupImage = new Texture(Gdx.files.internal("starmario50.png"));
        birdblueImage = new Texture(Gdx.files.internal("birdred.png"));
        birdredImage = new Texture(Gdx.files.internal("birdblue.png"));


        part1PipeUpImage = new Texture(Gdx.files.internal("brokenpipe1.png"));
        part2PipeUpImage = new Texture(Gdx.files.internal("brokenpipe2.png"));

        // create the obstacles array and spawn the first obstacle
        obstacles = new Array<Rectangle>();
        brokenobstacles = new Array<Rectangle>();
        powerups = new Array<Rectangle>();
        spawnPowerup();
        spawnObstacle();

        part1Image = new Texture(Gdx.files.internal("part1bird.png"));
        part2Image = new Texture(Gdx.files.internal("part2bird.png"));
        part3Image = new Texture(Gdx.files.internal("part3bird.png"));
        part4Image = new Texture(Gdx.files.internal("part4bird.png"));

        vulnerabilidad = true;
        changecolor = true;

    }



    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // begin a new batch
        game.batch.begin();
        game.batch.draw(backgroundImage, 0, 0);
        if(!dead){
            if(vulnerabilidad){
                game.batch.draw(birdImage, player.x, player.y);
            }else{
                if(changecolor){
                    game.batch.draw(birdblueImage, player.x, player.y);
                    changecolor = false;
                }else{
                    game.batch.draw(birdredImage, player.x, player.y);

                }


            }

        }else{
            game.batch.draw(part1Image,part1.x,part1.y);
            game.batch.draw(part2Image,part2.x,part2.y);
            game.batch.draw(part3Image,part3.x,part3.y);
            game.batch.draw(part4Image,part4.x,part4.y);
        }

        // DIbuixa els obstacles: Els parells son tuberia inferior,
        //els imparells tuberia superior
        for(int i = 0; i < obstacles.size; i++) {
            game.batch.draw(
                    i % 2 == 0 ? pipeUpImage : pipeDownImage,
                    obstacles.get(i).x, obstacles.get(i).y);
        }

        for(int i = 0; i < brokenobstacles.size; i++) {
            game.batch.draw(
                    i % 2 == 0 ? part1PipeUpImage : part2PipeUpImage,
                    brokenobstacles.get(i).x, brokenobstacles.get(i).y);
        }

        for(int i = 0; i < powerups.size; i++) {
            game.batch.draw(
                    i % 2 == 0 ? powerupImage : powerupImage,
                    powerups.get(i).x, powerups.get(i).y);
        }

        game.font.draw(game.batch, "Score: " + (int)score, 10, 470);
        game.batch.end();


        //--------------------------------LOGICA----------------------------------------

        score += Gdx.graphics.getDeltaTime();

        if (Gdx.input.justTouched() && !dead) {
            speedy = 400f;
            flapSound.play();
        }

        //Actualitza la posició del jugador amb la velocitat vertical
        player.y += speedy * Gdx.graphics.getDeltaTime();
        //Actualitza la velocitat vertical amb la gravetat
        speedy -= gravity * Gdx.graphics.getDeltaTime();

        // Comprova que el jugador no es surt de la pantalla.
        // Si surt per la part inferior, game over
        if (player.y > 480 - 45){
            player.y = 480 - 45;
            speedy = -speedy / 2;
        }

        /*if (player.y < 0 - 45) {
            //dead = true;
            player.y = 0 - 45;
            speedy = + speedy ;
        }*/

        if (player.y <  0) {
            dead = true;
            player.y = 0;
            speedy = + speedy ;
        }


        // Comprova si cal generar un obstacle nou
        if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000){
            spawnObstacle();
            changecolor = true;

        }
        if (TimeUtils.nanoTime() - lastObstacleTime3 > 15000000000l){
            spawnPowerup();
        }

        if (TimeUtils.nanoTime() - lastObstacleTime2 > 1490000000){
            if(dead){
                finaldead = true;
            }
        }



        // Mou els obstacles. Elimina els que estan fora de la pantalla
        // Comprova si el jugador colisiona amb un obstacle,
        // llavors game over
        Iterator<Rectangle> iter = obstacles.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Rectangle tuberia = iter.next();
            tuberia.x -= 250 * Gdx.graphics.getDeltaTime();
            if (tuberia.x < -64)
                iter.remove();
            if (tuberia.overlaps(player)) {

                if(vulnerabilidad){
                    dead = true;
                }

                //pipeXposition = tuberia.x;
                //pipeYposition = tuberia.y;
                if(i % 2 !=0){
                    spawnBrokenObstacle(tuberia.x,tuberia.y);
                    tuberia.y -=600;
                }else{
                    //spawnBrokenObstacle(tuberia.x,tuberia.y);
                    //tuberia.y -=600;
                }

            }

            //pipeXposition +=1;
            //pipeYposition +=1;

        Iterator<Rectangle> brokeniter = brokenobstacles.iterator();
        int j = 0;

            while (brokeniter.hasNext()){
                Rectangle tuberiarota = brokeniter.next();
                //tuberiarota.y = pipeYposition - 100;
                if(j % 2 ==0){
                    //tuberiarota.x = pipeXposition;
                    //tuberiarota.y = pipeYposition;
                    tuberiarota.x += 0.5;
                    tuberiarota.y += 0.5;
                }else{
                    tuberiarota.x += 0.5;
                    tuberiarota.y -= 0.5;
                }
            j++;
            }
            i++;
        }


        Iterator<Rectangle> poweriter = powerups.iterator();
        int k = 0;
        while (poweriter.hasNext()) {
            Rectangle powerup = poweriter.next();
            powerup.y = 480 / 2;
            powerup.x -= 5 ;
            //tuberia.x -= 250 * Gdx.graphics.getDeltaTime();
            if (powerup.x < -64)
                poweriter.remove();
            if (powerup.overlaps(player)) {
                vulnerabilidad = false;
                    powerup.y -=600;
                    poweriter.remove();
            }
        k++;
        }


        if(dead) {

            birdImage.dispose();
            if(oneTime) {
                explosion1.play();
                part1.x = player.x;
                part1.y = player.y;
                part2.x = player.x;
                part2.y = player.y;
                part3.x = player.x;
                part3.y = player.y;
                part4.x = player.x;
                part4.y = player.y;
                oneTime =false;
            }

            part1.x -= 1 ; part1.y += 1 ;
            part2.x += 1 ; part2.y += 1 ;
            part3.x -= 1 ; part3.y -= 1 ;
            part4.x += 1 ; part4.y -= 1 ;

        }

        if(finaldead){
            failSound.play();
            game.lastScore = (int)score;
            if(game.lastScore > game.topScore)
                game.topScore = game.lastScore;
            game.setScreen(new GameOverScreen(game));
            dispose();
        }


    }

    private void spawnObstacle() {
        // Calcula la alçada de l'obstacle aleatòriament
        float holey = MathUtils.random(50, 230);
        // Crea dos obstacles: Una tubería superior i una inferior
        Rectangle pipe1 = new Rectangle();
        pipe1.x = 800;
        pipe1.y = holey - 230;
        pipe1.width = 64;
        pipe1.height = 230;
        obstacles.add(pipe1);
        Rectangle pipe2 = new Rectangle();
        pipe2.x = 800;
        pipe2.y = holey + 200;
        pipe2.width = 64;
        pipe2.height = 230;
        obstacles.add(pipe2);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    private void spawnBrokenObstacle(float x , float y){
        // Calcula la alçada de l'obstacle aleatòriament
        //float holey = MathUtils.random(50, 230);
        // Crea dos obstacles: Una tubería superior i una inferior
        Rectangle brokenpipe1 = new Rectangle();
        brokenpipe1.x = x;
        brokenpipe1.y =  y;
        brokenpipe1.width = 64;
        brokenpipe1.height = 230;
        brokenobstacles.add(brokenpipe1);
        Rectangle brokenpipe2 = new Rectangle();
        brokenpipe2.x = x;
        brokenpipe2.y = y - 20;
        brokenpipe2.width = 64;
        brokenpipe2.height = 230;
        brokenobstacles.add(brokenpipe2);
        lastObstacleTime2 = TimeUtils.nanoTime();
    }

    private void spawnPowerup() {
        Rectangle powerup = new Rectangle();
        powerup.width = 34;
        powerup.height = 34;
        powerup.x = 1050;
        powerups.add(powerup);
        lastObstacleTime3 = TimeUtils.nanoTime();
    }


    public void spawnPart(float x,float y){
       /* Rectangle part1 = new Rectangle();
        Rectangle part2 = new Rectangle();
        Rectangle part3 = new Rectangle();
        Rectangle part4 = new Rectangle();

        part1.x = x; part1.y = y;
        part2.x = x; part2.y = y;
        part3.x = x; part3.y = y;
        part4.x = x; part4.y = y;*/

        lastObstacleTime = TimeUtils.nanoTime();

    }

    @Override
    public void resize(int width, int height) {
    }
    @Override
    public void show() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }
    @Override
    public void dispose() {
        backgroundImage.dispose();
        birdImage.dispose();
        pipeUpImage.dispose();
        powerupImage.dispose();
        pipeDownImage.dispose();
        flapSound.dispose();
        failSound.dispose();
    }
}