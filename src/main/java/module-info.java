// module com.example.ui {
//     requires javafx.controls;
//     requires javafx.fxml;

//     requires java.net.http;
//     requires javafx.graphics;

//     // Gson (automatic module name)

//     opens com.example.ui to javafx.fxml;

//     exports com.example.ui;
// }

// module com.example.ui {
//     requires javafx.controls;
//     requires javafx.fxml;
//     requires java.net.http;
//     requires javafx.graphics;

//     // Spring modules
//     requires org.springframework.core;       // spring-core
//     requires org.springframework.beans;      // spring-beans
//     requires org.springframework.context;    // spring-context
//     requires spring.messaging;               // spring-messaging
//     requires org.springframework.websocket;  // spring-websocket

//     opens com.example.ui to javafx.fxml;
//     exports com.example.ui;
// }

// module com.example.ui {
//     requires javafx.controls;
//     requires javafx.fxml;
//     requires java.net.http;
//     //requires javafx.graphics;

//     // Use automatic module names (from JARs)
//     requires spring.core;
//     requires spring.beans;
//     requires spring.context;
//     requires spring.messaging;
//     requires spring.websocket;
//     requires spring.web;

//     requires transitive javafx.graphics;

//     opens com.example.ui to javafx.fxml;
//     exports com.example.ui;
// }

module com.example.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires spring.core;
    requires spring.beans;
    requires spring.context;
    requires spring.messaging;
    requires spring.websocket;
    requires spring.web;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.databind;

    opens com.example.ui to javafx.fxml;
    exports com.example.ui;
}
