package mygame;

// Game Imports
import com.jme3.light.AmbientLight;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Geometry;
import com.jme3.ui.Picture;

/**
 * Game: CarniShooter
 * Date: 10/13/2018
 * Author: Dillan Cobb
 * About: Simple game, you have 3 shots and 3 cans to shoot. Weather or not you
 * knock all the cans down, you still lose. Just like at a real carnival.
 * 
 * CONTROLS
 * W - Move forward
 * A - Move left
 * S - Move back
 * D - Move right
 * LEFT MOUSE - Shoot ball
 */


public class Main extends SimpleApplication {

    // Private class variables
    // For physics
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape, counterColl, tableColl, table2Coll;
    private RigidBodyControl ballPhy;
    private RigidBodyControl canPhy, can2Phy, can3Phy;
    
    // For controlling the player
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false, 
            shoot = false;
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    
    // Counts the number of shots left
    private int ballsLeft = 3;
    
    // For the ball remaining display
    private BitmapText ch;
    
    // For displaying the finish message
    private boolean isDone = false;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        /** Set up Physics */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // Creates the Scene
            // Scene node
            Node sceneNode = new Node("sceneNode");

            // Loads the map of the scene
            Spatial room = assetManager.loadModel("Models/BallRoom/BallRoom.j3o");
            room.scale(1f, 1f, 1f);
            room.setLocalTranslation(0.0f, 0f, 0f);

            // Loads the light of the scene
            AmbientLight al = new AmbientLight();
            al.setColor(ColorRGBA.White.mult(1.0f));
            rootNode.addLight(al);

            // Counter Top
            Box counter = new Box(10,1.5f,1);
            Geometry counterBox = new Geometry("Box", counter);
            Material counterMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            counterMat.setColor("Color", ColorRGBA.Brown);
            counterBox.setMaterial(counterMat);
            counterBox.setLocalTranslation(0,1.5f,1);

            // 3 Tables for the cans
            Box table = new Box(1,1,1);
            Geometry table1 = new Geometry("Box", table);
            Geometry table2 = new Geometry("Box", table);
            Geometry table3 = new Geometry("Box", table);
            Material tableMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            tableMat.setColor("Color", ColorRGBA.Black);
            table1.setMaterial(tableMat);
            table2.setMaterial(tableMat);
            table3.setMaterial(tableMat);
            table1.setLocalTranslation(0,2,-6);
            table2.setLocalTranslation(-4,2,-5);
            table3.setLocalTranslation(4,2,-5);
            table1.scale(1,1.5f,1);

            rootNode.attachChild(sceneNode);
            
            // Create the cans
            Spatial can = assetManager.loadModel("Models/can/can.j3o");
            can.scale(0.3f);
            can.setLocalTranslation(-0.4f, 4f, -6);
            
            Spatial can2 = assetManager.loadModel("Models/can/can.j3o");
            can2.scale(0.3f);
            can2.setLocalTranslation(0.4f, 4f, -6);
            
            Spatial can3 = assetManager.loadModel("Models/can/can.j3o");
            can3.scale(0.3f);
            can3.setLocalTranslation(0f, 5f, -6);
        
        // Setting up collisions on the objects
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape(room);
        landscape = new RigidBodyControl(sceneShape, 0);
        room.addControl(landscape);
        
        CollisionShape counterShape = CollisionShapeFactory.createMeshShape(counterBox);
        counterColl = new RigidBodyControl(counterShape, 0);
        counterBox.addControl(counterColl);
        
        CollisionShape tableShape = CollisionShapeFactory.createMeshShape(table1);
        tableColl = new RigidBodyControl(tableShape, 0);
        table1.addControl(tableColl);
        
        CollisionShape tableSmShape = CollisionShapeFactory.createMeshShape(table2);
        table2Coll = new RigidBodyControl(tableSmShape, 0);
        table2.addControl(table2Coll);
        table3.addControl(table2Coll);
        
        canPhy = new RigidBodyControl(2f);
        can2Phy = new RigidBodyControl(2f);
        can3Phy = new RigidBodyControl(2f);
        can.addControl(canPhy);
        can2.addControl(can2Phy);
        can3.addControl(can3Phy);
        
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 1f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(new Vector3f(0,0,0));
        player.setPhysicsLocation(new Vector3f(0, 4, 7));
        
        // Adding all the physics objects to the rootnode
        rootNode.attachChild(room);
        rootNode.attachChild(counterBox);
        rootNode.attachChild(table1);
        rootNode.attachChild(table2);
        rootNode.attachChild(table3);
        rootNode.attachChild(can);
        rootNode.attachChild(can2);
        rootNode.attachChild(can3);
        bulletAppState.getPhysicsSpace().add(counterColl);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().add(tableColl);
        bulletAppState.getPhysicsSpace().add(table2Coll);
        bulletAppState.getPhysicsSpace().add(canPhy);
        bulletAppState.getPhysicsSpace().add(can2Phy);
        bulletAppState.getPhysicsSpace().add(can3Phy);
        
        // displays the ball count
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        ch = new BitmapText(guiFont, false);
        ch.setSize(40);
        ch.setText("" + ballsLeft);
        ch.setLocalTranslation(110, 90, 0);
        guiNode.attachChild(ch);
        
        initCrossHairs(); 
        initKeys();      
        showBall();
    }
    
    // Use for the custom controls
    private void initKeys() {
        inputManager.addMapping("Shoot",
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoot");
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
    }
    
    // Handling Controls
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Shoot") && !keyPressed) {
                makeBall();
            }

            if (name.equals("Left") && !keyPressed) {
                left = !left;
                right = false;
                up = false;
                down = false;
            } else if (name.equals("Right") && !keyPressed) {
                right = !right;
                left = false;
                up = false;
                down = false;
            } else if (name.equals("Up") && !keyPressed) {
                up = !up;
                right = false;
                left = false;
                down = false;
            } else if (name.equals("Down") && !keyPressed) {
                down = !down;
                right = false;
                up = false;
                left = false;
            }
        }
    };
    
    // + sign cross hair
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
            settings.getWidth() / 2 - ch.getLineWidth()/2,
            settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
        guiNode.attachChild(ch);
    }
    
    // Displays a little ball image
    protected void showBall() {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/ballimg.png", true);
        pic.setWidth(100);
        pic.setHeight(40);
        pic.setPosition(0, 50);
        guiNode.attachChild(pic);
    }
    
    // displays the end image for the finish fo the game
    protected void showEnd() {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/finish.png", true);
        pic.setWidth(settings.getWidth()/2);
        pic.setHeight(settings.getHeight()/2);
        pic.setPosition(settings.getWidth()/4, settings.getHeight()/4);
        guiNode.attachChild(pic);
    }
    
    // fires a ball from the players POV
    private void makeBall() {     
        if (ballsLeft > 0) {
            Spatial ballObj = assetManager.loadModel("Models/Ball/Ball.j3o");
            rootNode.attachChild(ballObj);
            ballObj.setLocalTranslation(cam.getLocation());
            ballPhy = new RigidBodyControl(1f);
            ballObj.addControl(ballPhy);
            ballObj.scale(0.3f);
            bulletAppState.getPhysicsSpace().add(ballPhy);
            ballPhy.setLinearVelocity(cam.getDirection().mult(50));
            
            ballsLeft--;
        }
    }
    
    @Override
        public void simpleUpdate(float tpf) {
            Vector3f cameraDirection = cam.getDirection();
            float camDirX = cameraDirection.x;
            float camDirY = cam.getLocation().y;
            float camDirZ = cameraDirection.z;
            
            camDir.set(camDirX / 10, camDirY, camDirZ / 10);
            camLeft.set(cam.getLeft()).divideLocal(10);
            walkDirection.set(0, 0, 0);
            if (left == true) {
                walkDirection.addLocal(camLeft);
            }
            if (right) {
                walkDirection.addLocal(camLeft.negate());
            }
            if (up) {
                walkDirection.addLocal(camDir);
            }
            if (down) {
                walkDirection.addLocal(camDir.negate());
            }
            player.setWalkDirection(walkDirection);
            cam.setLocation(player.getPhysicsLocation());
            
            ch.setText("" + ballsLeft);
            
            if (ballsLeft == 0) {
                if (!isDone) {
                    showEnd();
                    isDone = true;
                }
            }
        }
}
