//
//  DateManager.h
//  myCurrency
//
//  Created by Simone Abate on 29/02/24.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DateManager : NSObject

- (instancetype)init;

@property (nonatomic, strong) NSDate *yesterday;
@property (nonatomic, strong) NSDate *weekAgo;

@property (nonatomic, strong) NSDate *minimumDate;
@property (nonatomic, strong) NSDate *maximumDate;

@property (nonatomic, strong) NSString *fromDate;
@property (nonatomic, strong) NSString *toDate;


@property (nonatomic, strong) NSDateFormatter *dateFormatter;

@end

NS_ASSUME_NONNULL_END
