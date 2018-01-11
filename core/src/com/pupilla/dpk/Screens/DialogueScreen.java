package com.pupilla.dpk.Screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pupilla.dpk.Backend.Collision;
import com.pupilla.dpk.Backend.Constants;
import com.pupilla.dpk.Backend.Conversation;
import com.pupilla.dpk.Sprites.NPC;

/**
 * Created by orzech on 07.01.2018.
 */

public class DialogueScreen extends ApplicationAdapter implements Screen {

    private static final String TAG = "DialogueScreen";
    private Game game;
    private Stage stage;
    private SpriteBatch batch;

    private Table table;
    private Label name, text;
    private TextButton end;
    private Skin skin;
    private Label.LabelStyle whiteFont;

    private int index;
    private int width = 640, height;

    private BitmapFont bf;

    private Table innerTable, outerTable;
    private ScrollPane scrollPane;

    public DialogueScreen(Game game){
        this.game = game;
        batch = new SpriteBatch();
        table = new Table();
        innerTable = new Table();
        outerTable = new Table();
    }

    @Override
    public void show() {
        height = (width* Gdx.graphics.getHeight())/Gdx.graphics.getWidth();
        Viewport viewport = new FitViewport(width, height, new OrthographicCamera());
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal(Constants.skin));
        bf = new BitmapFont(Gdx.files.internal(Constants.font));
        whiteFont = new Label.LabelStyle(bf, Color.WHITE);

        end = new TextButton(Constants.end, skin);

        name = new Label("", whiteFont);
        text = new Label("", whiteFont);

        table.debug();

        prepareTable();

        addListeners();
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.end();
        stage.draw();
        stage.act();
    }

    @Override
    public void hide() {

    }

    private void addListeners(){
        end.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(PlayScreen.parent);
            }
        });
    }

    private void prepareTable(){
        index = getNPCindex();
        NPC npc = PlayScreen.NPCs.get(index);
        if(index!=999){
            name.setText(npc.name);
            table.add(name);
            table.row();
            Gdx.app.debug(TAG, npc.conversations.size()+"");
            text.setText(npc.conversations.get(0).text);
            text.setWrap(true);
            text.setFontScale(0.7f);
            text.setAlignment(Align.center);
            table.add(text).width(640);
            table.row();

            for(int i=0; i<npc.conversations.get(0).responses.length; i++){
                // if I have access to see response...
                if(npc.conversations.get(0).accessibility[i] && npc.conversations.get(0).nextDialogues.length!=0){
                    // create button with that response
                    TextButton response = new TextButton(npc.conversations.get(0).responses[i], skin);
                    response.getLabel().setWrap(true);
                    response.getLabel().setFontScale(0.6f);
                    if(npc.conversations.get(0).nextDialogues[i]!=999){
                        final int next = npc.conversations.get(0).nextDialogues[i];
                        response.addListener(new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y){
                                Gdx.app.debug(TAG, "next Dialogue: "+next);
                                setNewResponses(next);
                            }
                        });
                    }
                    innerTable.add(response).fill();
                    innerTable.row();
                }
            }

            innerTable.add(end).width(width-20);
            scrollPane = new ScrollPane(innerTable, skin);
            outerTable.add(scrollPane);
            outerTable.row();
            outerTable.setHeight(180);
            outerTable.setWidth(width-10);
            outerTable.setPosition(0,0);
            //table.setX(0);
            //table.setY(height-table.getHeight());
            table.setPosition(outerTable.getWidth()/2+10, outerTable.getHeight()+60);
            stage.addActor(table);
            stage.addActor(outerTable);
        }
    }

    private int getNPCindex(){
        for(int i=0; i<PlayScreen.NPCs.size(); i++){
            if(Collision.NPCname.equals(PlayScreen.NPCs.get(i).name)){
                return i;
            }
        }
        return 999;
    }

    private void setNewResponses(int nextDialogue){
        for(int i=0; i<stage.getActors().size; i++){
            if(stage.getActors().get(i).equals(table)){
                stage.getActors().removeIndex(i);
            }
            if(stage.getActors().get(i).equals(outerTable)){
                stage.getActors().removeIndex(i);
            }
        }
        innerTable = new Table();
        outerTable = new Table();
        table = new Table();
        table.debug();
        table.setPosition(width/2, height);
        NPC npc = PlayScreen.NPCs.get(index);
        Conversation dialogue = getDialogue(npc, nextDialogue);

        startQuest(dialogue.id);
        name = new Label(npc.name, whiteFont);
        text = new Label(dialogue.text, whiteFont);
        text.setWrap(true);
        text.setFontScale(0.7f);
        text.setAlignment(Align.center);
        table.add(name);
        table.row();
        table.add(text).width(640);
        table.row();

        for(int i=0; i<dialogue.responses.length; i++){
            // if I have access to see response...
            if(dialogue.accessibility[i] && dialogue.nextDialogues.length!=0){
                // create button with that response
                TextButton response = new TextButton(dialogue.responses[i], skin);
                response.getLabel().setWrap(true);
                response.getLabel().setFontScale(0.6f);
                if(dialogue.nextDialogues[i]!=999){
                    final int next = dialogue.nextDialogues[i];
                    response.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                            //Gdx.app.debug(TAG, "next Dialogue: "+next);
                            setNewResponses(next);
                        }
                    });
                }
                innerTable.add(response).fill();
                innerTable.row();
            }
        }

        innerTable.add(end).width(width-20);
        scrollPane = new ScrollPane(innerTable, skin);
        outerTable.add(scrollPane);
        outerTable.row();
        outerTable.setHeight(180);
        outerTable.setWidth(width-10);
        outerTable.setPosition(0,0);
        //table.setX(0);
        //table.setY(height-table.getHeight());
        table.setPosition(outerTable.getWidth()/2+10, outerTable.getHeight()+60);
        stage.addActor(table);
        stage.addActor(outerTable);
    }

    private Conversation getDialogue(NPC npc, int id){
        for(int i=0; i<npc.conversations.size(); i++){
            if(npc.conversations.get(i).id == id){
                return npc.conversations.get(i);
            }
        }
        return new Conversation();
    }

    /**
     * Checking whether that dialogue option starts new quest
     * @param dialogue
     */
    private void startQuest(int dialogue){

    }

    @Override
    public void dispose(){
        stage.dispose();
        batch.dispose();
        skin.dispose();
        bf.dispose();
    }
}
