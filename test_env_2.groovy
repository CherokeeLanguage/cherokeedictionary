import java.util.Properties

def props = new Properties()
new File(".env").withInputStream { props.load(it) }

println "DB_USER from file: " + props.getProperty("DB_USER")
println "DB_PASSWORD from file: " + props.getProperty("DB_PASSWORD")

println "System env DB_USER: " + System.getenv("DB_USER")
println "System env DB_PASSWORD: " + System.getenv("DB_PASSWORD")

println "System property DB_USER: " + System.getProperty("DB_USER")
println "System property DB_PASSWORD: " + System.getProperty("DB_PASSWORD")
