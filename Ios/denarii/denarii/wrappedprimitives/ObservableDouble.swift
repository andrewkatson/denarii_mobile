import Foundation

class ObservableDouble:  ObservableObject {
    @Published var value: Double = 0.0
    
    func setValue(_ newVal: Double) {
        self.value = newVal
    }
    
    func getValue() -> Double {
        return self.value
    }
}
