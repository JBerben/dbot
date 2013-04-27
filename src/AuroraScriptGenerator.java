import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

public class AuroraScriptGenerator {

	public AuroraScriptGenerator() {
	}

	public static void main(String[] args) throws Exception {
		File jarFile = new File(args[0]);
		if(!jarFile.exists())
			throw new RuntimeException("Jar not found: " + args[0]);
		File templateFile = new File(args[1]);
		if(!templateFile.exists())
			throw new RuntimeException("Template not found: " + args[1]);
		JavaClass templateClass = new ClassParser(new FileInputStream(
				templateFile), templateFile.getName()).parse();
		List<ClassGen> generatedClasses = new ArrayList<>();
		JarInputStream inputStream = new JarInputStream(new FileInputStream(
				jarFile));
		JarEntry entry;
		while((entry = inputStream.getNextJarEntry()) != null) {
			String entryName = entry.getName();
			if(!entryName.endsWith(".class"))
				continue;
			ByteArrayOutputStream dataArray = new ByteArrayOutputStream();
			byte[] dataBuffer = new byte[1024];
			int dataRead;
			while((dataRead = inputStream.read(dataBuffer)) != -1)
				dataArray.write(dataBuffer, 0, dataRead);
			byte[] data = dataArray.toByteArray();
			try {
				ClassParser parser = new ClassParser(new ByteArrayInputStream(
						data), "unknown");
				JavaClass javaClass = parser.parse();
				ClassGen gen = new ClassGen(javaClass);
				ClassGen template = createTemplateFor(gen, templateClass);
				if(template != null)
					generatedClasses.add(template);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		inputStream.close();

		FileOutputStream stream = new FileOutputStream(new File(args[2]));
		JarOutputStream out = new JarOutputStream(stream);
		for(ClassGen classGen : generatedClasses) {
			JarEntry jarEntry = new JarEntry(classGen.getClassName().replace(
					'.', '/')
					+ ".class");
			out.putNextEntry(jarEntry);
			out.write(classGen.getJavaClass().getBytes());
		}
		out.close();
		stream.close();
	}

	private static ClassGen createTemplateFor(ClassGen script,
			JavaClass template) {
		AnnotationEntryGen[] annotations = script.getAnnotationEntries();
		String name = null, author = null, description = null, category = "Other";
		double version = -1;
		boolean found = false;
		for(AnnotationEntryGen annotation : annotations) {
			if(!annotation.getTypeSignature().equals(
					"Lorg/darkstorm/runescape/script/ScriptManifest;"))
				continue;
			found = true;
			for(ElementValuePairGen valuePair : annotation.getValues()) {
				String valueName = valuePair.getNameString();
				ElementValueGen value = valuePair.getValue();
				if(valueName.equals("name"))
					name = value.stringifyValue();
				else if(valueName.equals("authors"))
					author = ((ArrayElementValueGen) value).getElementValues()
							.get(0).stringifyValue();
				else if(valueName.equals("description"))
					description = value.stringifyValue();
				else if(valueName.equals("version"))
					version = Double.parseDouble(value.stringifyValue());
				else if(valueName.equals("category"))
					category = value.stringifyValue().charAt(0)
							+ value.stringifyValue().substring(1).toLowerCase();
			}
			break;
		}
		if(!found)
			return null;
		System.out.println("Generating template for: " + script.getClassName());
		ClassGen classGen = new ClassGen(template.copy());
		String templateClassName = classGen.getClassName();
		classGen.setClassName("Aurora"
				+ script.getClassName().substring(
						script.getClassName().lastIndexOf('.') + 1));
		ConstantPoolGen constantPool = classGen.getConstantPool();
		for(int i = 0; i < constantPool.getSize(); i++) {
			Constant constant = constantPool.getConstant(i);
			if(constant instanceof ConstantUtf8) {
				ConstantUtf8 utf8 = (ConstantUtf8) constant;
				String string = utf8.getBytes();
				if(string.equals("<name>"))
					utf8.setBytes(name);
				else if(string.equals("<author>"))
					utf8.setBytes(author);
				else if(string.equals("<description>"))
					utf8.setBytes(description);
				else if(string.equals("<category>"))
					utf8.setBytes(category);
				else if(string.equals("<script>"))
					utf8.setBytes(script.getClassName());
				else if(string.equals(templateClassName))
					utf8.setBytes(classGen.getClassName());
			} else if(constant instanceof ConstantDouble) {
				ConstantDouble constantDouble = (ConstantDouble) constant;
				double value = constantDouble.getBytes();
				if(value == 1.337)
					constantDouble.setBytes(version);
			}
		}
		return classGen;
	}
}
