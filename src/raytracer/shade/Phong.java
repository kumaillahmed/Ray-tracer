package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Point;
import raytracer.math.Vec3;

public class Phong implements Shader {
    private final Shader inner;
    private final Color ambient;
    private final float diffuse;
    private final float specular;
    private final float shininess;

    public Phong(final Shader inner, final Color ambient, final float diffuse, final float specular,
            final float shininess) {

        // check if ambient color and inner shader are not null
        if (ambient == null) {
            throw new IllegalArgumentException("Error: Ambient color can't be null.");
        }
        if (inner == null) {
            throw new IllegalArgumentException("Error: Inner shader can't be null.");
        }

        if (Float.isNaN(diffuse) || Float.isNaN(specular) || Float.isNaN(shininess)) {
            throw new IllegalArgumentException("etc");
        }

        // initialize Phong shader with provided parameters
        this.inner = inner;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    @Override
    public Color shade(final Hit hit, final Trace trace) {

        // compute shading color from inner shader and multiply it by ambient color
        Color color = inner.shade(hit, trace).mul(ambient);

        // extract hit point and surface normal from Hit object
        Point hitPoint = hit.getPoint().sub(Vec3.ZERO);
        Vec3 normal = hit.getNormal();

        // iterate over each light source in scene
        for (LightSource light : trace.getScene().getLightSources()) {

            // calculate direction from hit point to light source and normalize it
            Vec3 lightDirec = light.getLocation().sub(hitPoint).normalized();

            // compute diffuse contribution by taking dot product of normal and light
            // direction
            float diffu = Math.max(0, normal.dot(lightDirec));

            // compute diffuse color by multiplying diffuse factor and inner shading color
            Color diffuColor = inner.shade(hit, trace)
                    .mul(new Color(diffu * diffuse, diffu * diffuse, diffu * diffuse));

            // compute reflection direction using reflection formula
            Vec3 reflec = normal.scale(2 * normal.dot(lightDirec)).sub(lightDirec);

            // compute specular contribution by taking dot product of reflection and light
            // direction
            float specu = (float) Math.pow(Math.max(0, reflec.dot(lightDirec)), shininess);

            // compute specular color by multiplying specular factor and light color
            Color specuColor = light.getColor().mul(new Color(specu * specular, specu * specular, specu * specular));

            // add diffuse and specular colors to shading color
            color = color.add(diffuColor).add(specuColor);
        }
        // return final shading color
        return color;
    }
}
