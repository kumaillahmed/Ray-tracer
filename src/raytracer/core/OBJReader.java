package raytracer.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import raytracer.core.def.Accelerator;
import raytracer.core.def.StandardObj;
import raytracer.math.Point;
import raytracer.math.Vec3;
import raytracer.geom.GeomFactory;

/**
 * Represents a model scan reader for the OBJ format
 */
public class OBJReader {

	/**
	 * Reads an OBJ scan and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param filename
	 *                    The scan to read the data from
	 * @param accelerator
	 *                    The target acceleration structure
	 * @param shader
	 *                    The shader which is used by all triangles
	 * @param scale
	 *                    The scale factor which is responsible for scaling the
	 *                    model
	 * @param translate
	 *                    A vector representing the translation coordinate with
	 *                    which
	 *                    all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *                                  If the filename is null or the empty string,
	 *                                  the accelerator
	 *                                  is null, the shader is null, the translate
	 *                                  vector is null,
	 *                                  the translate vector is not finite or scale
	 *                                  does not
	 *                                  represent a legal (finite) floating point
	 *                                  number
	 */
	public static void read(final String filename,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		read(new BufferedInputStream(new FileInputStream(filename)), accelerator, shader, scale, translate);
	}

	/**
	 * Reads an OBJ scan and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param in
	 *                    The InputStream of the data to be read.
	 * @param accelerator
	 *                    The target acceleration structure
	 * @param shader
	 *                    The shader which is used by all triangles
	 * @param scale
	 *                    The scale factor which is responsible for scaling the
	 *                    model
	 * @param translate
	 *                    A vector representing the translation coordinate with
	 *                    which
	 *                    all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *                                  If the InputStream is null, the accelerator
	 *                                  is null, the shader is null, the translate
	 *                                  vector is null,
	 *                                  the translate vector is not finite or scale
	 *                                  does not
	 *                                  represent a legal (finite) floating point
	 *                                  number
	 */
	public static void read(final InputStream in,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {

		Scanner scan = new Scanner(in);
		String[] element;
		String nextline;
		Point a, b, c;
		Float x, y, z;
		Point vertex;
		Obj triangle;
		List<Point> vList = new ArrayList<>();

		scan.useLocale(Locale.ENGLISH);

		while (scan.hasNextLine()) {

			nextline = scan.nextLine();

			if (!nextline.startsWith("v") && !nextline.startsWith("f")) {
				continue;
			}

			element = nextline.split(" ");

			if (nextline.startsWith("v") && element.length == 4) {

				x = Float.parseFloat(element[1]) * scale + translate.x();
				y = Float.parseFloat(element[2]) * scale + translate.y();
				z = Float.parseFloat(element[3]) * scale + translate.z();

				vertex = new Point(x, y, z);

				vList.add(vertex);

			}

			if (nextline.startsWith("f") && element.length == 4) {

				a = vList.get(Integer.parseInt(element[1]) - 1);
				b = vList.get(Integer.parseInt(element[2]) - 1);
				c = vList.get(Integer.parseInt(element[3]) - 1);

				triangle = new StandardObj(GeomFactory.createTriangle(a, b, c), shader);
				accelerator.add(triangle);
			}

		}
		scan.close();
	}
}
