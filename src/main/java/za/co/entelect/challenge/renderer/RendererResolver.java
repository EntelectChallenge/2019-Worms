package za.co.entelect.challenge.renderer;

import za.co.entelect.challenge.game.contracts.bootstrapper.GameEngineBootstrapper;
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer;
import za.co.entelect.challenge.game.contracts.renderer.RendererType;

public class RendererResolver {

    private GameEngineBootstrapper gameEngineBootstrapper;

    public RendererResolver(GameEngineBootstrapper gameEngineBootstrapper) {
        this.gameEngineBootstrapper = gameEngineBootstrapper;
    }

    public GameMapRenderer resolve(RendererType rendererType) {
        return gameEngineBootstrapper.getRenderer(rendererType);
    }
}
