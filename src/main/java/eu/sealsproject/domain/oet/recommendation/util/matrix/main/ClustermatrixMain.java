package eu.sealsproject.domain.oet.recommendation.util.matrix.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import eu.sealsproject.domain.oet.recommendation.Jama.Matrix;
import eu.sealsproject.domain.oet.recommendation.util.map.MapItem;
import eu.sealsproject.domain.oet.recommendation.util.map.MatrixMapping;


public class ClustermatrixMain {

	/**
	 * Main method used for building and serializing cluster matrix
	 * @param args
	 */
	public static void main(String[] args) {
		
//		double[] cluster = {0,0,0,0,0.5,0,0,0,0,0,0,0,0,0,0,0.5,
//				0.497,0,0,0,0.106,0,0,0,0,0,0,0,0,0,0,0.397,
//				0,0,0,0,0.5,0,0,0,0,0,0,0,0,0,0,0.5,
//				0,0,0.497,0,0.106,0,0,0,0,0,0,0,0,0,0,0.397,
//				0.125,0.138,0.183,0.140,0.064,0.059,0,0.175,0,0,0,0,0,0,0,0.116,
//				0,0,0,0,0.5,0,0,0,0,0,0,0,0,0,0,0.5,
//				0,0,0,0,0,0,0.5,0,0,0,0,0,0,0,0,0.5,
//				0,0,0,0,0.5,0,0,0,0,0,0,0,0,0,0,0.5,
//				0,0,0,0,0,0,0,0,0.5,0,0,0,0,0,0,0.5,
//				0,0,0,0,0.5,0,0,0,0,0,0,0,0,0,0,0.5,
//				0,0,0,0,0.177,0,0,0,0.177,0,0.406,0,0,0,0,0.24,
//				0,0,0,0,0,0,0,0,0,0,0,0.5,0,0,0,0.5,
//				0,0,0,0,0,0,0,0,0,0,0,0,0.5,0,0,0.5,
//				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0.5,0.5,
//				0,0,0,0,0,0,0,0,0,0,0,0,0,0.5,0,0.5,				
//				0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066,0.066};
//		
//		Matrix clusterMatrix = new Matrix(cluster,16);
//		clusterMatrix.setId("http://www.seals-project.eu/clusterMatrix");
//		clusterMatrix.serialize("src/main/resources/matrices/ClusterMatrix");
		
		LinkedList<Matrix> clusterComparisons = new LinkedList<Matrix>();
		
		double[] c1 = {1,1,1,1};
		Matrix olmc = new Matrix(c1, 2);
		olmc.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageModelConformance");
		MatrixMapping iolcM = new MatrixMapping();
		iolcM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		iolcM.addMapItem(new MapItem(1, "Alternatives"));
		olmc.setMapping(iolcM);
		clusterComparisons.add(olmc);
		
		
		
		double[] c2 = {1,0.166,1,
				6,1,1,
				1,1,1};
		Matrix olint = new Matrix(c2, 3);
		olint.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageInteroperability");
		MatrixMapping olintM = new MatrixMapping();
		olintM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageModelConformance"));
		olintM.addMapItem(new MapItem(1, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		olintM.addMapItem(new MapItem(2, "Alternatives"));
		olint.setMapping(olintM);
		clusterComparisons.add(olint);
		
		
		double[] c3 = {1,1,1,1};
		Matrix opa = new Matrix(c3, 2);
		opa.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingAccuracy");
		MatrixMapping opaM = new MatrixMapping();
		opaM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		opaM.addMapItem(new MapItem(1, "Alternatives"));
		opa.setMapping(opaM);
		clusterComparisons.add(opa);
		
		
		double[] c4 = {1,0.166,1,
				6,1,1,
				1,1,1};
		Matrix ointacc = new Matrix(c4, 3);
		ointacc.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyInterchangeAccuracy");
		MatrixMapping ointaccM = new MatrixMapping();
		ointaccM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingAccuracy"));
		ointaccM.addMapItem(new MapItem(1, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		ointaccM.addMapItem(new MapItem(2, "Alternatives"));
		ointacc.setMapping(ointaccM);
		clusterComparisons.add(ointacc);
		
		
		double[] c5 = {1,1,1,1,0.33,3,0.33,1,
				1,1,1,1,0.33,1,0.33,1,
				1,1,1,0.33,0.166,1,0.33,1,
				1,1,3,1,0.166,1,0.33,1,
				3,3,6,6,1,1,3,1,
				0.33,1,1,1,1,1,0.166,1,
				3,3,3,3,0.33,6,1,1,
				1,1,1,1,1,1,1,1};
		Matrix opr = new Matrix(c5, 8);
		opr.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness");
		MatrixMapping oprM = new MatrixMapping();
		oprM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageModelConformance"));
		oprM.addMapItem(new MapItem(1, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageInteroperability"));
		oprM.addMapItem(new MapItem(2, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingAccuracy"));
		oprM.addMapItem(new MapItem(3, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyInterchangeAccuracy"));
		oprM.addMapItem(new MapItem(4, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingTimeBehaviour"));
		oprM.addMapItem(new MapItem(5, "http://www.seals-project.eu/ontologies/QualityModel.owl#ReasoningAccuracy"));
		oprM.addMapItem(new MapItem(6, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		oprM.addMapItem(new MapItem(7, "Alternatives"));
		opr.setMapping(oprM);
		clusterComparisons.add(opr);
		
		
		double[] c6 = {1,1,1,1};
		Matrix optb = new Matrix(c6, 2);
		optb.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingTimeBehaviour");
		MatrixMapping optbM = new MatrixMapping();
		optbM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		optbM.addMapItem(new MapItem(1, "Alternatives"));
		optb.setMapping(optbM);
		clusterComparisons.add(optb);
		
		
		double[] c7 = {1,1,1,1};
		Matrix oaacc = new Matrix(c7, 2);
		oaacc.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyAlignmentAccuracy");
		MatrixMapping oaaccM = new MatrixMapping();
		oaaccM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyAlignmentAccuracy"));
		oaaccM.addMapItem(new MapItem(1, "Alternatives"));
		oaacc.setMapping(oaaccM);
		clusterComparisons.add(oaacc);
		
		
		double[] c8 = {1,1,1,1};
		Matrix racc = new Matrix(c8, 2);
		racc.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#ReasoningAccuracy");
		MatrixMapping raccM = new MatrixMapping();
		raccM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		raccM.addMapItem(new MapItem(1, "Alternatives"));
		racc.setMapping(raccM);
		clusterComparisons.add(racc);
		
		
		double[] c9 = {1,1,1,1};
		Matrix ssacc = new Matrix(c9, 2);
		ssacc.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#SemanticSearchAccuracy");
		MatrixMapping ssaccM = new MatrixMapping();
		ssaccM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#SemanticSearchAccuracy"));
		ssaccM.addMapItem(new MapItem(1, "Alternatives"));
		ssacc.setMapping(ssaccM);
		clusterComparisons.add(ssacc);
		
		
		double[] c10 = {1,1,1,1};
		Matrix rtb = new Matrix(c10, 2);
		rtb.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#ReasoningTimeBehaviour");
		MatrixMapping rtbM = new MatrixMapping();
		rtbM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		rtbM.addMapItem(new MapItem(1, "Alternatives"));
		rtb.setMapping(rtbM);
		clusterComparisons.add(rtb);
		
		
		double[] c11 = {1,1,3,1,
				1,1,3,1,
				0.33,0.33,1,1,
				1,1,1,1};
		Matrix sstb = new Matrix(c11, 4);
		sstb.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#SemanticSearchTimeBehaviour");
		MatrixMapping sstbM = new MatrixMapping();
		sstbM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		sstbM.addMapItem(new MapItem(1, "http://www.seals-project.eu/ontologies/QualityModel.owl#SemanticSearchAccuracy"));
		sstbM.addMapItem(new MapItem(2, "http://www.seals-project.eu/ontologies/QualityModel.owl#SemanticSearchTimeBehaviour"));
		sstbM.addMapItem(new MapItem(3, "Alternatives"));
		sstb.setMapping(sstbM);
		clusterComparisons.add(sstb);
		
		
		double[] c12 = {1,1,1,1};
		Matrix swsacc = new Matrix(c12, 2);
		swsacc.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#SWSDiscoveryAccuracy");
		MatrixMapping swsaccM = new MatrixMapping();
		swsaccM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#SWSDiscoveryAccuracy"));
		swsaccM.addMapItem(new MapItem(1, "Alternatives"));
		swsacc.setMapping(swsaccM);
		clusterComparisons.add(swsacc);
		
		double[] c13 = {1,1,1,1};
		Matrix eff = new Matrix(c13, 2);
		eff.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#EfficiencyInUse");
		MatrixMapping effM = new MatrixMapping();
		effM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#EfficiencyInUse"));
		effM.addMapItem(new MapItem(1, "Alternatives"));
		eff.setMapping(effM);
		clusterComparisons.add(eff);
		
		double[] c14 = {1,1,1,1};
		Matrix omtb = new Matrix(c14, 2);
		omtb.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyMatchingTimeBehaviour");
		MatrixMapping omtbM = new MatrixMapping();
		omtbM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyMatchingRobustness"));
		omtbM.addMapItem(new MapItem(1, "Alternatives"));
		omtb.setMapping(omtbM);
		clusterComparisons.add(omtb);
		
		
		double[] c15 = {1,1,1,1};
		Matrix omr = new Matrix(c15, 2);
		omr.setId("http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyMatchingRobustness");
		MatrixMapping omrM = new MatrixMapping();
		omrM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyMatchingTimeBehaviour"));
		omrM.addMapItem(new MapItem(1, "Alternatives"));
		omr.setMapping(omrM);
		clusterComparisons.add(omr);
		
		
		double[] c16 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
				1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		Matrix alt = new Matrix(c16, 15);
		alt.setId("Alternatives");
		MatrixMapping altM = new MatrixMapping();
		altM.addMapItem(new MapItem(0, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageModelConformance"));
		altM.addMapItem(new MapItem(1, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyLanguageInteroperability"));
		altM.addMapItem(new MapItem(2, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingAccuracy"));
		altM.addMapItem(new MapItem(3, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyInterchangeAccuracy"));
		altM.addMapItem(new MapItem(4, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingTimeBehaviour"));
		altM.addMapItem(new MapItem(5, "http://www.seals-project.eu/ontologies/QualityModel.owl#ReasoningAccuracy"));
		altM.addMapItem(new MapItem(6, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyProcessingRobustness"));
		altM.addMapItem(new MapItem(7, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyAlignmentAccuracy"));
		altM.addMapItem(new MapItem(8, "http://www.seals-project.eu/ontologies/QualityModel.owl#SemanticSearchAccuracy"));
		altM.addMapItem(new MapItem(9, "http://www.seals-project.eu/ontologies/QualityModel.owl#ReasoningTimeBehaviour"));
		altM.addMapItem(new MapItem(10, "http://www.seals-project.eu/ontologies/QualityModel.owl#SemanticSearchTimeBehaviour"));
		altM.addMapItem(new MapItem(11, "http://www.seals-project.eu/ontologies/QualityModel.owl#SWSDiscoveryAccuracy"));
		altM.addMapItem(new MapItem(12, "http://www.seals-project.eu/ontologies/QualityModel.owl#EfficiencyInUse"));
		altM.addMapItem(new MapItem(13, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyMatchingTimeBehaviour"));
		altM.addMapItem(new MapItem(14, "http://www.seals-project.eu/ontologies/QualityModel.owl#OntologyMatchingRobustness"));
		alt.setMapping(altM);
		clusterComparisons.add(alt);
		
		
		
		File dataFile = new File("src/main/resources/matrices/ClusterComparisons");
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(dataFile));
			oos.writeObject(clusterComparisons);
			oos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error in comparison matirces serialization");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error in comparison matirces serialization");
			e.printStackTrace();
		}
	}
}
