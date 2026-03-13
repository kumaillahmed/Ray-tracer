package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;

public class CheckerBoard implements Shader {
    private final Shader shaderA;
    private final Shader shaderB;
    private final float scale;

    public CheckerBoard(Shader shaderA, Shader shaderB, float scale) {
        if (shaderA == null || shaderB == null) {
            throw new IllegalArgumentException("Shader arguments cannot be null.");
        }

        if (scale < 0 || Float.isInfinite(scale)) {
            throw new IllegalArgumentException("Scale value must be a non-negative number.");
        }

        if (Float.isNaN(scale)) {
            throw new IllegalArgumentException("etc");
        }

        this.shaderA = shaderA;
        this.shaderB = shaderB;
        this.scale = scale;
    }

    @Override
    public Color shade(Hit hit, Trace trace) {
        float u = hit.getUV().x();
        float v = hit.getUV().y();

        int x = (int) (Math.floor(u / scale) + Math.floor(v / scale));
        Shader selectedShader = (x % 2 == 0) ? shaderA : shaderB;

        return selectedShader.shade(hit, trace);
    }
}
