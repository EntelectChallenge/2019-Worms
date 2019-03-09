package za.co.entelect.challenge.game.contracts.renderer;

public interface RendererFactory {
    GameMapRenderer getRenderer(RendererType rendererType);
}
