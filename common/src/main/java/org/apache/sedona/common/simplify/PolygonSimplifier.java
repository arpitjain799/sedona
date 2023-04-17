package org.apache.sedona.common.simplify;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

import static org.apache.sedona.common.simplify.BaseSimplifier.geometryFactory;

public class PolygonSimplifier {
    public static Geometry simplify(Polygon geom, boolean preserveCollapsed, double epsilon) {
        LinearRing exteriorRing = geom.getExteriorRing();
        int minPointsExternal = preserveCollapsed ? 4 : 0;

        LinearRing simplifiedExterior =
                geometryFactory.createLinearRing(CoordinatesSimplifier.simplifyInPlace(exteriorRing.getCoordinates(), epsilon, minPointsExternal));

        if (simplifiedExterior.getNumPoints() < 4) {
            return simplifiedExterior;
        }
        else {
            List<LinearRing> interiorRings = new ArrayList<>();
            for (int i = 0; i < geom.getNumInteriorRing() ; i++) {
                LinearRing interiorRing = geom.getInteriorRingN(i);
                Coordinate[] simplifiedInterior = CoordinatesSimplifier.simplifyInPlace(interiorRing.getCoordinates(), epsilon, minPointsExternal);
                if (simplifiedInterior.length >= 4) {
                    interiorRings.add(geometryFactory.createLinearRing(simplifiedInterior));
                }
            }
            return geometryFactory.createPolygon(simplifiedExterior, interiorRings.toArray(new LinearRing[0]));
        }
    }
}
